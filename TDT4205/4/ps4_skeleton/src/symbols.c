#include <vslc.h>
#include "assert.h"

/* Global symbol table and string list */
symbol_table_t *global_symbols;
char **string_list;
size_t string_list_len;
size_t string_list_capacity;

static symbol_t *create_symbol(symbol_table_t *ctx, struct node *node, symtype_t sym_type, char *name);
static void find_globals ( void );
static void bind_names ( symbol_table_t *local_symbols, node_t *root );
static void print_symbol_table ( symbol_table_t *table, int nesting );
static void destroy_symbol_tables ( void );

static void string_list_init(void);
static void string_list_ensure_capacity(void);
static size_t add_string ( char* string );
static void print_string_list ( void );
static void destroy_string_list ( void );

/* External interface */

/* Creates a global symbol table, and local symbol tables for each function.
 * While building the symbol tables:
 *  - All usages of symbols are bound to their symbol table entries.
 *  - All strings are entered into the string_list
 */
void create_tables ( void )
{
    find_globals();

    for (size_t i = 0; i < global_symbols->n_symbols; i++) {
        symbol_t *sym = global_symbols->symbols[i];
        if (sym->type != SYMBOL_FUNCTION)
            continue;
        /*
         * Function syntax:
         * FUNC identifier ( parameter_list ) statement
         *       child 0         child 1       child 2
         */

        struct node *function_node = sym->node;
        /* Create symbols for function parameters */
        node_t *param_list = function_node->children[1];
        for (size_t j = 0; j < param_list->n_children; j++) {
            node_t *func_param = param_list->children[j];
            create_symbol(sym->function_symtable, func_param, SYMBOL_PARAMETER, func_param->data);
        }
        /* Bind names for the statements in the function */
        bind_names(sym->function_symtable, function_node->children[2]);
    }
}

/* Prints the global symbol table, and the local symbol tables for each function.
 * Also prints the global string list.
 * Finally prints out the AST again, with bound symbols.
 */
void print_tables ( void )
{
    print_symbol_table ( global_symbols, 0 );
    printf("\n == STRING LIST == \n");
    print_string_list ();
    printf("\n == BOUND SYNTAX TREE == \n");
    print_syntax_tree ();
}

/* Destroys all symbol tables and the global string list */
void destroy_tables ( void )
{
    destroy_symbol_tables ( );
    destroy_string_list ( );
}

/* Internal matters */

static symbol_t *create_symbol(symbol_table_t *ctx, struct node *node, symtype_t sym_type, char *name)
{
    symbol_t *symbol = malloc(sizeof(symbol_t));
    symbol->node = node;
    symbol->name = name;
    symbol->type = sym_type;
    symbol->function_symtable = NULL;
    symbol_table_insert(ctx, symbol);
    return symbol;
}

/* Goes through all global declarations in the syntax tree, adding them to the global symbol table.
 * When adding functions, local symbol tables are created, and symbols for the functions parameters are added.
 */
static void find_globals ( void )
{
    global_symbols = symbol_table_init ( );
    string_list_init();

    /* NOTE(Nicolai): Assuming a root node must be of type LIST */
    assert(root->type == LIST);

    for (size_t i = 0; i < root->n_children; i++) {
        node_t *this = root->children[i];
        switch (this->type) {
        case FUNCTION: {
            /*
             * Function syntax:
             * FUNC identifier ( parameter_list ) statement
             *       child 0         child 1       child 2
             */
            symbol_t *sym = create_symbol(global_symbols, this, SYMBOL_FUNCTION, (char *)this->children[0]->data);
            sym->function_symtable = symbol_table_init();
            sym->function_symtable->hashmap->backup = global_symbols->hashmap;
            break;
        }
        case GLOBAL_DECLARATION: {
            node_t *decl_list = this->children[0];
            for (size_t i = 0; i < decl_list->n_children; i++) {
                node_t *decl = decl_list->children[i];
                if (decl->type == IDENTIFIER_DATA)
                    create_symbol(global_symbols, decl, SYMBOL_GLOBAL_VAR, decl->data);
                else if (decl->type == ARRAY_INDEXING)
                    /* fill the symbol for the first childing being the IDENTIFIER_DATA node */
                    create_symbol(global_symbols, decl->children[0], SYMBOL_GLOBAL_ARRAY, decl->children[0]->data);
                else
                    assert(false && "Global declaration neither variable nor array???");
            }
            break;
        }
        default:
            break;
        }
    }
}

/* A recursive function that traverses the body of a function, and:
 *  - Adds variable declarations to the function's local symbol table.
 *  - Pushes and pops local variable scopes when entering and leaving blocks.
 *  - Binds all other IDENTIFIER_DATA nodes to the symbol it references.
 *  - Moves STRING_DATA nodes' data into the global string list,
 *    and replaces the node with a STRING_LIST_REFERENCE node.
 *    This node's data is a pointer to the char* stored in the string list.
 */
static void bind_names ( symbol_table_t *local_symbols, node_t *node )
{
    switch (node->type) {
    case BLOCK: {
        /* The block has a local declaration list if n_children == 2.*/
        if (node->n_children == 2) {
            symbol_hashmap_t *new_local_symbols = symbol_hashmap_init();
            new_local_symbols->backup = local_symbols->hashmap;
            local_symbols->hashmap = new_local_symbols;

            node_t *local_decls = node->children[0];
            assert(local_decls->type == LIST);
            /* Traverse all local declarations.
             * Must be done in a nested loop because of syntax like this being legal:
             * var x, y
             * var z
             */
            for (size_t i = 0; i < local_decls->n_children; i++) {
                node_t *current_decl_list = local_decls->children[i];
                for (size_t j = 0; j < current_decl_list->n_children; j++) {
                    node_t *decl = current_decl_list->children[j];
                    create_symbol(local_symbols, decl, SYMBOL_LOCAL_VAR, (char *)decl->data);
                }
            }

            bind_names(local_symbols, node->children[1]);
            local_symbols->hashmap = local_symbols->hashmap->backup;
            symbol_hashmap_destroy(new_local_symbols);
        } else {
            bind_names(local_symbols, node->children[0]);
        }
        break;
    }
    case IDENTIFIER_DATA: {
        //symbol_t *sym = create_symbol(local_symbols, node, SYMBOL_LOCAL_VAR, node->data);
        symbol_t *sym = symbol_hashmap_lookup(local_symbols->hashmap, node->data);
        if (sym == NULL) {
            fprintf(stderr, "Expected to find a symbol for '%s', but found no symbols...\n", 
                    (char *)node->data);
            /* Should probably save the fact we had an error. Alternatively exit. */
        } else {
            node->symbol = sym;
        }
        break;
    }
    case STRING_DATA: {
        size_t position = add_string(node->data);
        /* Change STRING_DATA to a STRING_LIST_REFERENCE. Data owned by the string list. */
        node->type = STRING_LIST_REFERENCE;
        node->data = (void *)position;
        break;
    }
    default:
        /* This case me only be relevant for nodes of type LIST, but*/
        for (size_t i = 0; i < node->n_children; i++) {
            node_t *this = node->children[i];
            bind_names(local_symbols, this);
        }
    }
}

/* Prints the given symbol table, with sequence number, symbol names and types.
 * When printing function symbols, its local symbol table is recursively printed, with indentation.
 */
static void print_symbol_table ( symbol_table_t *table, int nesting )

{
    for (size_t i = 0; i < table->n_symbols; i++) {
        symbol_t *sym = table->symbols[i];
        if (nesting > 0) {
            char nesting_buf[nesting + 1]; // VLA yolo
            memset(nesting_buf, ' ', sizeof(char) * nesting);
            nesting_buf[nesting] = 0;
            printf("%s", nesting_buf);
        }
        printf("%zu: %s(%s)\n", sym->sequence_number, SYMBOL_TYPE_NAMES[sym->type], sym->name);
        if (sym->type == SYMBOL_FUNCTION)
            print_symbol_table(sym->function_symtable, nesting + 1);
    }
}


static void destroy_symbol(symbol_t *sym)
{
    if (sym->type == SYMBOL_FUNCTION) {
        symbol_table_t *st = sym->function_symtable;
        for (size_t i = 0; i < st->n_symbols; i++)
            destroy_symbol(st->symbols[i]);

        free(st->symbols);
        symbol_hashmap_destroy(st->hashmap);
        free(sym->function_symtable);
    }
    free(sym);
}

/* Frees up the memory used by the global symbol table, all local symbol tables, and their symbols */
static void destroy_symbol_tables ( void )
{
    for (size_t i = 0; i < global_symbols->n_symbols; i++)
        destroy_symbol(global_symbols->symbols[i]);

    symbol_hashmap_destroy(global_symbols->hashmap);
}

static void string_list_init(void)
{
    string_list_len = 0;
    string_list_capacity = 8;
    string_list = malloc(sizeof(char *) * string_list_capacity);
}

static void string_list_ensure_capacity(void)
{
    if (string_list_len >= string_list_capacity) {
        string_list_capacity *= 2;
        string_list = realloc(string_list, sizeof(char *) * string_list_capacity);
    }
}

/* Adds the given string to the global string list, resizing if needed.
 * Takes ownership of the string, and returns its position in the string list.
 */
static size_t add_string ( char *string )
{
    string_list_ensure_capacity();
    string_list[string_list_len] = string;
    return string_list_len++;  // postfix increment
}

/* Prints all strings added to the global string list */
static void print_string_list ( void )
{
    for (size_t i = 0; i < string_list_len; i++)
        printf("%zu: %s\n", i, string_list[i]);
}

/* Frees all strings in the global string list, and the string list itself */
static void destroy_string_list ( void )
{
    for (size_t i = 0; i < string_list_len; i++)
        free(string_list[i]);
    free(string_list);
}
