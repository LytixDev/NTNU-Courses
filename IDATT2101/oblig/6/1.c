#include <stdio.h>      // fprintf, printf, fopen
#include <stdbool.h>    // bool type
#include <stdint.h>     // uint32_t type
#include <stdlib.h>     // exit
#include <string.h>     // memset

#define u32 uint32_t
#define u32_MAX (u32)(1 << 31)
#define ERROR_EOF (u32_MAX)
#define ERROR_STACK (u32_MAX)
#define DUMMY_INDEX (u32_MAX)   // not particularly elegant, but works
#define IS_DUMMY_NODE(node) (node->node_index == DUMMY_INDEX)


struct node_t {
    u32 node_index;
    struct node_t *next;
};

/* 
 * graphs are repsented as a "neighbour list".
 * every node has an index in a regular array where
 * the corresponding value is the first node of a
 * linked list where each node contains the index
 * of an edge in the graph.
 */
struct graph_t {
    struct node_t **nodes;
    u32 node_count;
    u32 edge_count;
};

/* simple stack for ints */
struct stack_t {
    u32 *data;
    size_t pos;
    size_t capacity;
};

/* globals */
/* 
 * when the node for an index does NOT have any edges,
 * its corresponding value is set to the dummy_node as 
 * NULL represents that the node does not exist in the
 * graph.
 */
struct node_t dummy_node;


static struct stack_t *malloc_stack(size_t capacity)
{
    struct stack_t *stack = malloc(sizeof(struct stack_t));
    stack->data = malloc(sizeof(u32) * capacity);
    stack->pos = 0;
    stack->capacity = capacity;

    return stack;
}

static void free_stack(struct stack_t *stack)
{
    free(stack->data);
    free(stack);
}

static void stack_push(struct stack_t *stack, u32 value)
{
    if (stack->pos == stack->capacity) {
        fprintf(stderr, "stack full!");
        return;
    }

    stack->data[stack->pos++] = value;
}

static u32 stack_pop(struct stack_t *stack)
{
    if (stack->pos == 0)
        return ERROR_STACK;

    return stack->data[--stack->pos];
}

static bool stack_is_empty(struct stack_t *stack)
{
    return stack->pos == 0;
}

static void print_graph(struct graph_t graph)
{
    for (u32 i = 0; i < graph.node_count; i++) {
        printf("node %d:\t", i);
        struct node_t *next = graph.nodes[i];
        while (next != NULL && !IS_DUMMY_NODE(next)) {
            printf("%d ", next->node_index);
            next = next->next;
        }

        putchar('\n');
    }
}

static void insert_edge_node(struct node_t **nodes, u32 node_index, u32 index_to_edge_node)
{
    struct node_t *this = malloc(sizeof(struct node_t));
    this->node_index = index_to_edge_node;
    this->next = NULL;

    /* insert new edgeing node at the beginning of the linked list */
    struct node_t *head = nodes[node_index];
    if (head == NULL) {
        nodes[node_index] = this;
    } else {
        this->next = head; 
        nodes[node_index] = this;
    }
}

static u32 get_next_int(FILE *fp)
{
    char ch;
    char buf[128];
    u32 i = 0;

    /* remove all leading whitespace */
    while ((ch = fgetc(fp)) == ' ') {
    }

    if (ch == EOF)
        return ERROR_EOF;

    buf[i++] = ch;

    do {
        ch = fgetc(fp);
        if (ch == ' ') {
            buf[i] = 0;
            break;
        } else if (ch == '\n') {
            break;
        } else if (i >= 128) {
            fprintf(stderr, "Error parsing file: node longer than 128 chars\n");
            exit(1);
            break;
        }

        buf[i++] = ch;
    } while (ch != EOF);

    if (ch == EOF)
        return ERROR_EOF;

    return atoi(buf);
}

static bool parse_file_into_graph(char *file_name, struct graph_t *graph)
{
    char ch;
    FILE *fp;
    fp = fopen(file_name, "r");
    if (fp == NULL)
        return true;

    u32 node_count, edge_count, largest_index, capacity;
    node_count = get_next_int(fp);
    edge_count = get_next_int(fp);
    largest_index = node_count;
    capacity = largest_index * 2 > u32_MAX ? u32_MAX : largest_index * 2;

    struct node_t **nodes = malloc(sizeof(struct node_t *) * capacity);

    while (1) {
        u32 node_index = get_next_int(fp);
        if (node_index == ERROR_EOF)
            break;
        if (nodes[node_index] == NULL)
            nodes[node_index] = &dummy_node;

        u32 index_to_edge_node = get_next_int(fp);
        if (index_to_edge_node == ERROR_EOF)    // error here means input is not in proper format
            return true;

        largest_index = node_index > largest_index ? node_index : largest_index;
        if (largest_index > capacity) {
            capacity = largest_index * 2 > u32_MAX ? u32_MAX : largest_index * 2;
            nodes = realloc(nodes, sizeof(struct node_t *) * capacity);
        }

        insert_edge_node(nodes, node_index, index_to_edge_node);
    }

    graph->nodes = nodes;
    graph->node_count = node_count;
    graph->edge_count = edge_count;
    return false;
}

static struct graph_t *transpose_graph(struct graph_t original)
{
    struct graph_t *transposed = malloc(sizeof(struct graph_t));
    transposed->nodes = malloc(sizeof(struct node *) * original.node_count);
    transposed->node_count = original.node_count;
    transposed->edge_count = original.edge_count;

    /* reverse all connections */
    for (u32 node = 0; node < original.node_count; node++) {
        struct node_t *goes_to = original.nodes[node];
        while (goes_to != NULL && !IS_DUMMY_NODE(goes_to)) {
            insert_edge_node(transposed->nodes, goes_to->node_index, node);
            goes_to = goes_to->next;
        }
    }
    return transposed;
}

static void DFS(struct graph_t graph, u32 start_node, bool *visited)
{
    u32 node;
    struct stack_t *stack = malloc_stack(graph.edge_count);
    stack_push(stack, start_node);

    while (!stack_is_empty(stack)) {
        node = stack_pop(stack);
        if (!visited[node]) {
            visited[node] = true;
            printf("%d ", node);
            struct node_t *n = graph.nodes[node];
            while (n != NULL && !IS_DUMMY_NODE(n)) {
                stack_push(stack, n->node_index);
                n = n->next;
            }
        }
    }
}

static void kosarajo_visit(struct graph_t graph, u32 node, bool *visited, struct stack_t *stack)
{
    visited[node] = true;
    struct node_t *next = graph.nodes[node];
    while (next != NULL) {
        if (!IS_DUMMY_NODE(next))
            if (!visited[next->node_index])
                kosarajo_visit(graph, next->node_index, visited, stack);

        next = next->next;
    }

    stack_push(stack, node);
}

static void kosaraju(struct graph_t graph)
{
    printf("Components\tNodes\n");
    struct stack_t *stack = malloc_stack(graph.edge_count);
    bool visited[graph.node_count];
    memset(visited, false, graph.node_count);        // set all nodes to visited is false
    
    for (u32 i = 0; i < graph.node_count; i++)
        if (!visited[i]) 
            kosarajo_visit(graph, i, visited, stack);

    struct graph_t transposed = *transpose_graph(graph);
    /* reuse old visited table for second run of kosarajo_visit on transposed graph */
    memset(visited, false, graph.node_count);
    
    /* process all edges as they're popped from the stack */
    u32 component = 1;
    while (!stack_is_empty(stack)) {
        u32 node = stack_pop(stack);
        if (!visited[node]) {
            printf("%d\t\t", component);
            DFS(transposed, node, visited);
            putchar('\n');
            component++;
        }
    }
}

int main()
{
    struct graph_t graph;
    char buf[512];
    char ch;
    u32 i = 0;

    /* init dummy node */
    dummy_node.next = NULL;
    dummy_node.node_index = DUMMY_INDEX;

    printf("Filename\n>>> ");
    while ((ch = fgetc(stdin)) != '\n') {
        if (i == 512)
            break;
        buf[i++] = ch;
    }
    buf[i] = 0;

    bool err = parse_file_into_graph(buf, &graph);
    if (err) {
        fprintf(stderr, "Could not parse file: %s\n", buf);
        exit(1);
    }

    kosaraju(graph);
}
