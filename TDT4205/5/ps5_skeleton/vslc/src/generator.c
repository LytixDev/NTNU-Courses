#include "symbols.h"
#include "vslc.h"

// This header defines a bunch of macros we can use to emit assembly to stdout
#include "emit.h"

// In the System V calling convention, the first 6 integer parameters are passed in registers
#define NUM_REGISTER_PARAMS 6
static const char *REGISTER_PARAMS[6] = {RDI, RSI, RDX, RCX, R8, R9};

// Takes in a symbol of type SYMBOL_FUNCTION, and returns how many parameters the function takes
#define FUNC_PARAM_COUNT(func) ((func)->node->children[1]->n_children)

static void generate_stringtable ( void );
static void generate_global_variables ( void );
static void generate_function ( symbol_t *function );
static void generate_expression ( node_t *expression );
static void generate_statement ( node_t *node );
static void generate_main ( symbol_t *first );

/* Entry point for code generation */
void generate_program ( void )
{
    generate_stringtable ( );
    generate_global_variables ( );

    // This directive announces that the following assembly belongs to the executable code .text section.
    DIRECTIVE ( ".text" );
    // For each function in global_symbols, generate it using generate_function ()
    symbol_t *main_func = NULL;
    for (size_t i = 0; i < global_symbols->n_symbols; i++) {
        symbol_t *sym = global_symbols->symbols[i];
        if (sym->type == SYMBOL_FUNCTION) {
            if (main_func == NULL)
                main_func = sym;
            generate_function(sym);
        }
    }

    // In VSL, the topmost function in a program is its entry point.
    // We want to be able to take parameters from the command line,
    // and have them be sent into the entry point function.
    //
    // Due to the fact that parameters are all passed as strings,
    // and passed as the (argc, argv)-pair, we need to make a wrapper for our entry function.
    // This wrapper handles string -> int64_t conversion, and is already implemented.
    // call generate_main ( <entry point function symbol> );
    assert(main_func != NULL);
    generate_main(main_func);
}

/* Prints one .asciz entry for each string in the global string_list */
static void generate_stringtable ( void )
{
    // This section is where read-only string data is stored
    // It is called .rodata on Linux, and "__TEXT, __cstring" on macOS
    DIRECTIVE ( ".section %s", ASM_STRING_SECTION );

    // These strings are used by printf
    DIRECTIVE ( "intout: .asciz \"%s\"", "%ld" );
    DIRECTIVE ( "strout: .asciz \"%s\"", "%s" );
    // This string is used by the entry point-wrapper
    DIRECTIVE ( "errout: .asciz \"%s\"", "Wrong number of arguments" );

    for (size_t i = 0; i < string_list_len; i++) {
        char *str = string_list[i];
        DIRECTIVE("string%zu:\t .asciz %s", i, str);
    }
}

/* Prints .zero entries in the .bss section to allocate room for global variables and arrays */
static void generate_global_variables ( void )
{
    // This section is where zero-initialized global variables lives
    // It is called .bss on linux, and "__DATA, __bss" on macOS
    DIRECTIVE ( ".section %s", ASM_BSS_SECTION );
    DIRECTIVE ( ".align 8" );

    // Give each a label you can find later, and the appropriate size.
    // Regular variables are 8 bytes, while arrays are 8 bytes per element.
    // Remember to mangle the name in some way, to avoid collisions if a variable is called e.g. "main"

    // For example to set aside 16 bytes and label it .myBytes:
    // DIRECTIVE ( ".myBytes: .zero 16" )
    for (size_t i = 0; i < global_symbols->n_symbols; i++) {
        symbol_t *sym = global_symbols->symbols[i];
        if (sym->type == SYMBOL_FUNCTION)
            break;

        size_t size = 8;
        if (sym->type == SYMBOL_GLOBAL_ARRAY) {
            assert(sym->node->type == ARRAY_INDEXING);
            assert(sym->node->n_children == 2);
            node_t *array_len_node = sym->node->children[1];
            size_t *array_len = (size_t *)array_len_node->data;
            size = 8 * (*array_len);
        }
        DIRECTIVE(".%s: .zero %zu", sym->name, size);
    }
}

/* Global variable you can use to make the functon currently being generated accessible from anywhere */
static symbol_t *current_function;

/* Prints the entry point. preamble, statements and epilouge of the given function */
static void generate_function ( symbol_t *function )
{
    LABEL(".%s", function->name);
    PUSHQ(RBP);
    MOVQ(RSP, RBP);

    size_t register_params = FUNC_PARAM_COUNT(function);
    /* All parameters after the first 6 will be pushed to the stack by the caller */
    if (register_params > 6)
        register_params = 6;
    for (size_t i = 0; i < register_params; i++)
        PUSHQ(REGISTER_PARAMS[i]);

    /* Make space for local variables on the stack */
    for (size_t i = 0; i < function->function_symtable->n_symbols; i++) {
        symbol_t *sym = function->function_symtable->symbols[i];
        if (sym->type == SYMBOL_LOCAL_VAR)
            PUSHQ("$0"); // initialize to 0
    }

    /* Generete function body */
    current_function = function;
    generate_statement(function->node->children[2]);

    MOVQ("$0", RAX); // For functions without return statements, store 0 in RAX
    EMIT(".%s_return:", function->name);
    EMIT("leave"); // Equivalent to `mov esp, ebp` followed by `pop ebp`
    RET;
}

/* Get the relative stack offset from the base pointer given a sequence */
static ssize_t get_stack_offset_from_sequence(symbol_t *function, ssize_t sequence)
{
    /* sequence starts at 0 */
    ssize_t param_count = FUNC_PARAM_COUNT(function);
    /* sequence is a parameter */
    if (sequence < param_count) {
        /* sequence is a caller pushed stack parameter */
        if (sequence >= 6) {
            ssize_t item_offset = sequence - 6 + 2; // +2 because of callers RBP and return addr.
            return item_offset * 8;
        }
        /* sequence must be a callee pushed parameter */
        return (sequence + 1) * -8;
    }

    ssize_t callee_stack_pushed_param_count = param_count > 6 ? 6 : param_count;
    ssize_t local_var_number = sequence - param_count;
    return (callee_stack_pushed_param_count + local_var_number + 1) * -8;
}

static void generate_function_call ( node_t *call )
{
    symbol_t *function = call->children[0]->symbol;
    size_t param_count = FUNC_PARAM_COUNT(function);
    size_t register_params = param_count > 6 ? 6 : param_count;
    node_t *params = call->children[1];
    assert(params->n_children == param_count); // NOTE: Not actually enforced 
    for (size_t i = 0; i < register_params; i++) {
        generate_expression(params->children[i]);
        MOVQ(RAX, REGISTER_PARAMS[i]);
    }
    /* stack parameters */
    if (param_count > register_params) {
        for (size_t i = param_count; i > 6; i--) {
            generate_expression(params->children[i - 1]);
            PUSHQ(RAX);
        }
    }

    EMIT("call .%s", function->name);

    /* Move the stack pointer back */
    if (param_count > register_params) {
        size_t stack_params_offset = (param_count - 6) * 8;
        EMIT("addq $%zu, %s", stack_params_offset, RSP);
    }
}

static void generate_binary_expression(node_t *expression)
{
    assert(expression->data != NULL);
    char *operator = expression->data;
    /* Generate LHS */
    generate_expression(expression->children[0]);
    /* Next gen will modify RAX, so we must first store the result on the stack */
    PUSHQ(RAX);
    generate_expression(expression->children[1]);
    MOVQ(RAX, RCX);
    /*
     * We actually don't need to worry about other stuff being pushed to the stack in the meantime
     * since any potenial push will always have a corresponding pop.
     */
    POPQ(RAX);

    if (strcmp(operator, "+") == 0) {
        ADDQ(RCX, RAX);
    } else if (strcmp(operator, "-") == 0) {
        SUBQ(RCX, RAX);
    } else if (strcmp(operator, "*") == 0) {
        IMULQ(RCX, RAX);
    } else if (strcmp(operator, "/") == 0) {
        /* Apparently need to clear RDX to avoid overflow as IDIVQ uses RDX:RAX */
        EMIT("xorq %s, %s", RDX, RDX);
        IDIVQ(RCX);
    } else if (strcmp(operator, ">>") == 0) {
        SAR(CL, RAX);
    } else if (strcmp(operator, "<<") == 0) {
        SAL(CL, RAX);
    }
}

/* Generates code to evaluate the expression, and place the result in %rax */
static void generate_expression ( node_t *expression )
{
    if (expression == NULL)
        return;
    switch (expression->type) {
    case NUMBER_DATA:
        EMIT("movq $%d, %s", *(int *)expression->data, RAX);
        break;
    case IDENTIFIER_DATA: {
        /* Load value of global variable */
        if (expression->symbol->type == SYMBOL_GLOBAL_VAR) {
            EMIT("movq .%s(%s), %s", expression->symbol->name, RIP, RAX);
            break;
        }
        /* SYMBOL_PARAMETER or SYMBOL_LOCAL_VAR */
        ssize_t relative_offset = get_stack_offset_from_sequence(current_function, 
                                                                expression->symbol->sequence_number);
        EMIT("movq %zd(%s), %s", relative_offset, RBP, RAX);
        break;
    }
    case EXPRESSION: {
        if (expression->n_children == 2) {
            generate_binary_expression(expression);
        } else {
            /* Unary expression */
            assert(expression->n_children == 1);
            char *operator = expression->data;
            assert(strcmp(operator, "-") == 0); // NOTE: I don't think there are any other unary exprs.
            generate_expression(expression->children[0]);
            NEGQ(RAX);
        }
        break;
    }
    case FUNCTION_CALL:
        generate_function_call(expression);
        break;
    case ARRAY_INDEXING: {
        assert(expression->n_children == 2); 
        node_t *index_expr = expression->children[1];
        generate_expression(index_expr);
        node_t *var = expression->children[0];
        char *var_name = (char *)var->data;
        EMIT("leaq .%s(%s), %s", var_name, RIP, RCX); // load the base of the array at RCX
        EMIT("leaq (%s, %s, 8), %s", RCX, RAX, RCX); // calculate final array index address
        EMIT("MOVQ (%s), %s", RCX, RAX);
        break;
    }
    default:
        assert(false && "generate_expression(): node type not handled");
        break;
    }
}

static void generate_assignment_statement ( node_t *statement )
{
    // You can assign to both local variables, global variables and function parameters.
    // Use the IDENTIFIER_DATA's symbol to find out what kind of symbol you are assigning to.
    // The left hand side of an assignment statement may also be an ARRAY_INDEXING node.
    assert(statement->n_children == 2);
    generate_expression(statement->children[1]); // stores the result into RAX

    node_t *identifier = statement->children[0];
    if (identifier->type == ARRAY_INDEXING) {
        /* Calculate the array index address */
        node_t *index_expr = identifier->children[1];
        generate_expression(index_expr);
        node_t *array_var = identifier->children[0];
        char *var_name = (char *)array_var->data;
        EMIT("leaq .%s(%s), %s", var_name, RIP, RCX); // load the base of the array at RCX
        // NOTE: if the index is 0 then this is redundant...
        EMIT("leaq (%s, %s, 8), %s", RCX, RAX, RCX); // calculate final array index address
        PUSHQ(RCX);
        /* Calculate the new value */
        generate_expression(statement->children[1]);
        POPQ(RCX); // array index address
        EMIT("MOVQ %s, (%s)", RAX, RCX);
        return;
    }

    assert(identifier->type == IDENTIFIER_DATA);

    /* Global variables */
    if (identifier->symbol->type == SYMBOL_GLOBAL_VAR) {
        EMIT("leaq .%s(%s), %s", (char *)identifier->data, RIP, RCX);
        EMIT("MOVQ %s, (%s)", RAX, RCX);
        return;
    }

    /* Parameters and local variables */
    ssize_t offset = get_stack_offset_from_sequence(current_function,
                                                    identifier->symbol->sequence_number);
    EMIT("movq %s, %zd(%s)", RAX, offset, RBP);
}

static void generate_print_statement ( node_t *statement )
{
    // Remember to call safe_printf instead of printf

    assert(statement->n_children == 1);
    node_t *list = statement->children[0];
    for (size_t i = 0; i < list->n_children; i++) {
        node_t *node = list->children[i];
        switch (node->type) {
        case STRING_LIST_REFERENCE: {
            // NOTE: for some reason node->data is NULL ????
            size_t string_list_pos = (size_t)node->data;
            EMIT("leaq strout(%s), %s", RIP, RDI); // put format as first parameter
            EMIT("leaq string%zu(%s), %s", string_list_pos, RIP, RSI); // put string as second param
            break;
        }
        case NUMBER_DATA:
            EMIT("leaq intout(%s), %s", RIP, RDI);
            EMIT("movq $%d, %s", *(int *)node->data, RSI);
            break;
        case IDENTIFIER_DATA: {
            symbol_t *sym = node->symbol;
            if (sym->type == SYMBOL_GLOBAL_VAR) {
                generate_expression(node);
                EMIT("leaq intout(%s), %s", RIP, RDI);
                EMIT("movq %s, %s", RAX, RSI);
                break;
            }
            // NOTE: Assuming local vars or parameters must be integers
            ssize_t offset = get_stack_offset_from_sequence(current_function, sym->sequence_number);
            EMIT("leaq intout(%s), %s", RIP, RDI);
            EMIT("movq %zd(%s), %s", offset, RBP, RSI);
            break;
        }
        default:
            // NOTE: Assuming this will just be any int expression
            generate_expression(node);
            EMIT("leaq intout(%s), %s", RIP, RDI);
            EMIT("movq %s, %s", RAX, RSI);
            break;
        }

        EMIT("call safe_printf");
    }

    /* ascii of '\n' is 0x0A */
    MOVQ("$0x0A", RDI);
    EMIT("call putchar");
}

static void generate_return_statement ( node_t *statement )
{
    if (statement->n_children == 1)
        generate_expression(statement->children[0]);
    /* NOTE: This will result in a couple of unecesary jumps, but oh well ! */
    EMIT("jmp .%s_return", current_function->name);
}

/* Recursively generate the given statement node, and all sub-statements. */
static void generate_statement ( node_t *node )
{
    switch(node->type) {
    case BLOCK:
    case LIST:
        for (size_t i = 0; i < node->n_children; i++)
            generate_statement(node->children[i]);
        break;
    case ASSIGNMENT_STATEMENT:
        generate_assignment_statement(node);
        break;
    case PRINT_STATEMENT:
        generate_print_statement(node);
        break;
    case RETURN_STATEMENT:
        generate_return_statement(node);
        break;
    case FUNCTION_CALL:
        generate_function_call(node);
        break;
    case EXPRESSION:
        generate_expression(node);
        break;
    default:
        // TODO_LOG("generate_statement(): node type not handled");
        break;
    }
}

static void generate_safe_printf ( void )
{
    LABEL ( "safe_printf" );

    PUSHQ ( RBP );
    MOVQ ( RSP, RBP );
    // This is a bitmask that abuses how negative numbers work, to clear the last 4 bits
    // A stack pointer that is not 16-byte aligned, will be moved down to a 16-byte boundary
    ANDQ ( "$-16", RSP );
    EMIT ( "call printf" );
    // Cleanup the stack back to how it was
    MOVQ ( RBP, RSP );
    POPQ ( RBP );
    RET;
}

static void generate_main ( symbol_t *first )
{
    // Make the globally available main function
    LABEL ( "main" );

    // Save old base pointer, and set new base pointer
    PUSHQ ( RBP );
    MOVQ ( RSP, RBP );

    // Which registers argc and argv are passed in
    const char* argc = RDI;
    const char* argv = RSI;

    const size_t expected_args = FUNC_PARAM_COUNT ( first );

    SUBQ ( "$1", argc ); // argc counts the name of the binary, so subtract that
    EMIT ( "cmpq $%ld, %s", expected_args, argc );
    JNE ( "ABORT" ); // If the provdied number of arguments is not equal, go to the abort label

    if (expected_args == 0)
        goto skip_args; // No need to parse argv

    // Now we emit a loop to parse all parameters, and push them to the stack,
    // in right-to-left order

    // First move the argv pointer to the vert rightmost parameter
    EMIT( "addq $%ld, %s", expected_args*8, argv );

    // We use rcx as a counter, starting at the number of arguments
    MOVQ ( argc, RCX );
    LABEL ( "PARSE_ARGV" ); // A loop to parse all parameters
    PUSHQ ( argv ); // push registers to caller save them
    PUSHQ ( RCX );

    // Now call strtol to parse the argument
    EMIT ( "movq (%s), %s", argv, RDI ); // 1st argument, the char *
    MOVQ ( "$0", RSI ); // 2nd argument, a null pointer
    MOVQ ( "$10", RDX ); //3rd argument, we want base 10
    EMIT ( "call strtol" );

    // Restore caller saved registers
    POPQ ( RCX );
    POPQ ( argv );
    PUSHQ ( RAX ); // Store the parsed argument on the stack

    SUBQ ( "$8", argv ); // Point to the previous char*
    EMIT ( "loop PARSE_ARGV" ); // Loop uses RCX as a counter automatically

    // Now, pop up to 6 arguments into registers instead of stack
    for ( size_t i = 0; i < expected_args && i < NUM_REGISTER_PARAMS; i++ )
        POPQ ( REGISTER_PARAMS[i] );

    skip_args:

    EMIT ( "call .%s", first->name );
    MOVQ ( RAX, RDI ); // Move the return value of the function into RDI
    EMIT ( "call exit" ); // Exit with the return value as exit code

    LABEL ( "ABORT" ); // In case of incorrect number of arguments
    EMIT ( "leaq errout(%s), %s", RIP, RDI );
    EMIT ( "call puts" ); // print the errout string
    MOVQ ( "$1", RDI );
    EMIT ( "call exit" ); // Exit with return code 1

    generate_safe_printf();

    // Declares global symbols we use or emit, such as main, printf and putchar
    DIRECTIVE ( "%s", ASM_DECLARE_SYMBOLS );
}
