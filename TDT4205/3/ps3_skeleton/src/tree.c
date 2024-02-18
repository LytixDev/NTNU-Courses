#define NODETYPES_IMPLEMENTATION
//#include <vslc.h>
#include <math.h>
#include "../include/vslc.h"

// Global root for abstract syntax tree
node_t *root;

// Declarations of internal functions, defined further down
static void node_print ( node_t *node, int nesting );
static void destroy_subtree ( node_t *discard );
static node_t* simplify_subtree ( node_t *node );

// Outputs the entire syntax tree to the terminal
void print_syntax_tree ( void )
{
    if ( getenv("GRAPHVIZ_OUTPUT") != NULL )
        graphviz_node_print ( root );
    else
        node_print ( root, 0 );
}

// Cleans up the entire syntax tree
void destroy_syntax_tree ( void )
{
    destroy_subtree ( root );
    root = NULL;
}

// Modifies the syntax tree, performing constant folding where possible
void simplify_tree ( void )
{
    root = simplify_subtree( root );
}

// Initialize a node with type, data, and children
node_t* node_create ( node_type_t type, void *data, size_t n_children, ... )
{
    node_t* result = malloc ( sizeof ( node_t ) );

    // Initialize every field in the struct
    *result = (node_t) {
        .type = type,
        .n_children = n_children,
        .children = (node_t **) malloc ( n_children * sizeof ( node_t * ) ),

        .data = data,
    };

    // Read each child node from the va_list
    va_list child_list;
    va_start ( child_list, n_children );
    for ( size_t i = 0; i < n_children; i++ )
        result->children[i] = va_arg ( child_list, node_t * );
    va_end ( child_list );

    return result;
}

// Append an element to the given LIST node, returns the list node
node_t* append_to_list_node ( node_t* list_node, node_t* element )
{
    assert ( list_node->type == LIST );

    // Calculate the minimum size of the new allocation
    size_t min_allocation_size = list_node->n_children + 1;

    // Round up to the next power of two
    size_t new_allocation_size = 1;
    while ( new_allocation_size < min_allocation_size ) new_allocation_size *= 2;

    // Resize the allocation
    list_node->children = realloc ( list_node->children, new_allocation_size * sizeof(node_t *) );

    // Insert the new element and increase child count by 1
    list_node->children[list_node->n_children] = element;
    list_node->n_children++;

    return list_node;
}

// Prints out the given node and all its children recursively
static void node_print ( node_t *node, int nesting )
{
    printf ( "%*s", nesting, "" );

    if ( node == NULL )
    {
        printf ( "(NULL)\n");
        return;
    }

    printf ( "%s", node_strings[node->type] );

    // For nodes with extra data, print the data with the correct type
    if ( node->type == IDENTIFIER_DATA ||
         node->type == EXPRESSION ||
         node->type == RELATION ||
         node->type == STRING_DATA)
    {
        printf ( "(%s)", (char *) node->data );
    }
    else if ( node->type == NUMBER_DATA )
    {
        printf ( "(%ld)", *(int64_t *) node->data );
    }

    putchar ( '\n' );

    // Recursively print children, with some more indentation
    for ( size_t i = 0; i < node->n_children; i++ )
        node_print ( node->children[i], nesting + 1 );
}

// Frees the memory owned by the given node, but does not touch its children
static void node_finalize ( node_t *discard )
{
    if ( discard == NULL )
        return;

    // Only free data if the data field is owned by the node
    switch ( discard->type )
    {
        case IDENTIFIER_DATA:
        case NUMBER_DATA:
        case STRING_DATA:
            free ( discard->data );
        default:
            break;
    }
    free ( discard->children );
    free ( discard );
}

// Recursively frees the memory owned by the given node, and all its children
static void destroy_subtree ( node_t *discard )
{
    if ( discard == NULL )
        return;

     for ( size_t i = 0; i < discard->n_children; i++ )
        destroy_subtree ( discard->children[i] );
     node_finalize ( discard );
}

// Recursively replaces EXPRESSION nodes representing mathematical operations
// where all operands are known integer constants
static node_t* constant_fold_node ( node_t *node )
{
    // TODO: 2.2 Perform constant folding on the given node if possible:
    //
    // - type == EXPRESSION
    // - all children of the node are of type NUMBER_DATA
    //
    // In that case, you can extract the int64_t values stored in the childrens' data fields,
    // and apply the operator stored in node->data on the values.
    // (Remember to use strcmp when comparing the string stored in node->data against operators)
    //
    // You should implement the following operators: "+" "-" "*" "/" "<<" and ">>"
    // Take care to implement both binary minus and unary minus (negation).
    //
    // If constant folding is possible, you can return a single NUMBER_DATA node with the resulting value.
    // Remember to clean up any discarded nodes.
    //
    // NB: Remember that all NUMBER_DATA nodes need to have their data field stored on the heap.
    if (node->type != EXPRESSION)
        return node;

    int64_t numbers[node->n_children];
    for (size_t i = 0; i < node->n_children; i++) {
        node_t *child = node->children[i];
        if (child->type != NUMBER_DATA)
            return node;
        numbers[i] = *(int64_t *)child->data;
    }

    char *operator = node->data;
    assert(operator != NULL && "COSNTANT FOLDING: Operator is NULL");

    int64_t result;
    /* Assuming the operators have sufficiently many children */
    if (strcmp(operator, "+") == 0) {
        result = numbers[0] + numbers[1];
    } else if (strcmp(operator, "-") == 0) {
        if (node->n_children == 1)
            result = -numbers[0];
        else
            result = numbers[0] - numbers[1];
    } else if (strcmp(operator, "*") == 0) {
        result = numbers[0] * numbers[1];
    } else if (strcmp(operator, "/") == 0) {
        //TODO: should probably handle division by zero case
        result = numbers[0] / numbers[1];
    } else if (strcmp(operator, "<<") == 0) {
        result = numbers[0] << numbers[1];
    } else if (strcmp(operator, ">>") == 0) {
        result = numbers[0] >> numbers[1];
    } else {
        assert(false && "COSNTANT FOLDING: Operator not recognized");
    }

    /* Free children */
    for (size_t i = 0; i < node->n_children; i++) {
        node_t *child = node->children[i];
        destroy_subtree(child);
    }

    /* Free data and change subtree root EXPRESSION to a NUMBER_DATA node */
    node->type = NUMBER_DATA;
    node->n_children = 0;
    node->data = malloc(sizeof(int64_t));
    *(int64_t *)node->data = result;

    return node;
}

// Recursively replaces multiplication and division by powers of two, with bitshifts
static node_t* peephole_optimize_node ( node_t* node )
{
    // TODO: 2.3 Perform a peephole optimization on EXPRESSION nodes
    // that perform multiplication or division by powers of 2
    //
    // These expressions can be replaced by bitshifting, which is a more efficient operation.
    // Some example conversions:
    //
    //  - A * 4 becomes A << 2
    //  - B / 16 becomes B >> 4
    //  - C * 32 becomes C << 5
    //
    // As a special case, if you are multiplying or dividing by 1, you don't need to do any shifting.
    //
    //  - A * 1 becomes just A
    //  - B / 1 becomes just B
    //
    // return the root of the new expression tree, and rememeber to clean up any detached nodes.

    /* Requirements */
    if (!(node->type == EXPRESSION && node->n_children == 2))
        return node;
    if (!(node->children[0]->type == IDENTIFIER_DATA && node->children[1]->type == NUMBER_DATA))
        return node;

    /* make sure operator is * or / */
    char *operator = node->data;
    assert(operator != NULL && "PEEPHOLE: Operator is NULL");
    if (strcmp(operator, "*") != 0 && strcmp(operator, "/") != 0)
        return node;

    node_t *left = node->children[0];
    node_t *right = node->children[1];
    /* edge case: divide or muliply by 1 */
    if (*(int64_t *)right->data == 1) {
        node->type = left->type;
        node->data = left->data;
        node->n_children = 0;
        free(left->children);
        free(left);
        node_finalize(right);
        return node;
    }

    if (*(int64_t *)right->data % 2 != 0)
        return node;
    *(int64_t *)right->data = (uint64_t )log2(*(int64_t *)right->data);

    /* This allocation will probably be lost */
    node->data = malloc(sizeof(char) * 3);
    ((char *)node->data)[2] = 0;
    if (strcmp(operator, "*") == 0) {
        ((char *)node->data)[0] = '<';
        ((char *)node->data)[1] = '<';
    } else {
        ((char *)node->data)[0] = '>';
        ((char *)node->data)[1] = '>';
    }

    return node;
}

static node_t* simplify_subtree( node_t* node )
{
    // TODO: 2.1 Implmement a recursive function that traverses the subtree rooted at node and simplifies it
    //
    // The simplification should be done in a bottom-up order.
    // In other words, this function should be recursively applied to all node's children,
    // before attempting to simplify the node itself.
    //
    // The two possible simplications that you should attempt to apply are:
    //  - constant_fold_node
    //  - peephole_optimize_node
    //
    // return the root of the new simplified subtree, or the same node if no simplifiction was possible.

    if (node == NULL)
        return NULL;

     for (size_t i = 0; i < node->n_children; i++)
        simplify_subtree(node->children[i]);
    
    /* 
     * I have implemented each optimization as so they simply return an unchanged node if no 
     * optimizations are possible.
     */
    node = constant_fold_node(node);
    node = peephole_optimize_node(node);

    return node;
}
