/* Written by Nicolai H. Brand 2022 */

#include <stdlib.h>     // malloc, free, realloc, atoi
#include <stdio.h>      // printf, fprintf, putchar
#include <stdbool.h>    // bool type
#include <string.h>     // memset


/* min-heap queue inspired by: https://docs.python.org/3/library/heapq.html */
struct heapq_t {
    struct node_t **items; 
    int size;
    int capacity;
};

struct node_t {
    int index;
    int cost;
    struct node_t *next;
};

/* 
 * ad-hoc datastructure to store information given by performing dijkstra 
 */
struct shortest_path {
    int total_cost;
    int previous_index;
};

/* 
 * graphs are repsented as a "neighbour list".
 * every node has an index in a regular array where
 * the corresponding value is the first node of a
 * linked list
 */
struct graph_t {
    struct node_t **nodes;
    int node_count;
    int edge_count;
};


#define HEAPQ_STARTING_CAPACITY 32
#define ERROR_EOF -1
#define INF (1 << 30)

#define heapq_left_child_idx(parent_idx) (parent_idx * 2 + 1)
#define heapq_right_child_idx(parent_idx) (parent_idx * 2 + 2)
#define heapq_parent_idx(child_idx) ((child_idx - 1) / 2)

#define heapq_has_left(idx, size) (heapq_left_child_idx(idx) < size)
#define heapq_has_right(idx, size) (heapq_right_child_idx(idx) < size)


static inline int heapq_left_child(struct heapq_t *hq, int idx)
{
    return hq->items[heapq_left_child_idx(idx)]->cost;
}

static inline int heapq_right_child(struct heapq_t *hq, int idx)
{
    return hq->items[heapq_right_child_idx(idx)]->cost;
}

static inline int heapq_parent(struct heapq_t *hq, int idx)
{
    return hq->items[heapq_parent_idx(idx)]->cost;
}

static void swap(struct heapq_t *hq, int a, int b)
{
    struct node_t *tmp = hq->items[a];
    hq->items[a] = hq->items[b];
    hq->items[b] = tmp;
}

static void ensure_capacity(struct heapq_t *hq)
{
    if (hq->size == hq->capacity) {
        hq->capacity *= 2;
        hq->items = realloc(hq->items, hq->capacity * sizeof(struct node_t *));
    }
}

static void heapify_up(struct heapq_t *hq)
{
    int idx = hq->size - 1;
    int parent_idx = heapq_parent_idx(idx);
    /* keep "repearing" heap as long as parent is greater than child */
    while (parent_idx >= 0 && hq->items[parent_idx]->cost > hq->items[idx]->cost) {
        swap(hq, parent_idx, idx);
        /* walk upwards */
        idx = heapq_parent_idx(idx);
        parent_idx = heapq_parent_idx(idx);
    }
}

static void heapify_down(struct heapq_t *hq)
{
    int idx = 0;
    int min_idx;
    while (heapq_has_left(idx, hq->size)) {
        min_idx = heapq_left_child_idx(idx);
        if (heapq_has_right(idx, hq->size) && heapq_right_child(hq, idx) < hq->items[min_idx]->cost)
            min_idx = heapq_right_child_idx(idx);

        if (hq->items[idx]->cost < hq->items[min_idx]->cost) {
            break;
        } else {
            swap(hq, idx, min_idx);
            idx = min_idx;
        }
    }
}

struct node_t *heapq_get(struct heapq_t *hq, int idx)
{
    if (idx < 0 || idx >= hq->size)
        return NULL;

    return hq->items[idx];
}

struct node_t *heapq_pop(struct heapq_t *hq)
{
    struct node_t *item = heapq_get(hq, 0);
    if (item == NULL)
        return NULL;

    hq->items[0] = hq->items[--hq->size];
    heapify_down(hq);
    return item;
}

void heapq_push(struct heapq_t *hq, int node_idx, int total_cost)
{
    ensure_capacity(hq);
    struct node_t *item = malloc(sizeof(struct node_t));
    item->index = node_idx;
    item->cost = total_cost;
    hq->items[hq->size++] = item;
    heapify_up(hq);
}

struct heapq_t *heapq_malloc()
{
    struct heapq_t *hq = malloc(sizeof(struct heapq_t));
    hq->size = 0;
    hq->capacity = HEAPQ_STARTING_CAPACITY;
    hq->items = malloc(HEAPQ_STARTING_CAPACITY * sizeof(struct node_t *));
    return hq;
}

void heapq_free(struct heapq_t *hq)
{
    free(hq->items);
    free(hq);
}

void heapq_print(struct heapq_t *hq)
{
    for (int i = 0; i < hq->size; i++)
        printf("(%d - %d)\n", hq->items[i]->index, hq->items[i]->cost);
    putchar('\n');
}

static int get_next_int(FILE *fp)
{
    char ch;
    char buf[128];
    int i = 0;

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

void graph_free(struct graph_t *graph)
{
    struct node_t *node_i, *prev;
    for (int i = 0; i < graph->node_count; i++) {
        node_i = graph->nodes[i];
        while (node_i != NULL) {
            prev = node_i;
            node_i = node_i->next;
            free(prev);
        }
    }
    free(graph->nodes);
}

void graph_insert(struct graph_t *graph, int node_index, int edge_index, int cost)
{
    struct node_t *node = malloc(sizeof(struct node_t));
    node->index = edge_index;
    node->cost = cost;

    struct node_t *head = graph->nodes[node_index];
    if (head == NULL) {
        graph->nodes[node_index] = node;
        return;
    }

    /* use new node as linkedlist head */
    node->next = head;
    graph->nodes[node_index] = node;
}

bool parse_file_into_graph(char *file_name, struct graph_t *graph)
{
    char ch;
    FILE *fp;
    fp = fopen(file_name, "r");
    if (fp == NULL)
        return true;

    int node_count = get_next_int(fp);
    int edge_count = get_next_int(fp);

    graph->nodes = malloc(sizeof(struct node_t *) * node_count);;

    while (1) {
        int node_index = get_next_int(fp);
        if (node_index == ERROR_EOF)
            break;
        int edge_index = get_next_int(fp);
        int cost = get_next_int(fp);
        graph_insert(graph, node_index, edge_index, cost);
    }

    fclose(fp);
    graph->node_count = node_count;
    graph->edge_count = edge_count;
    return false;
}

void graph_print(struct graph_t *graph)
{
    printf("total nodes: %d, total edges: %d\n", graph->node_count, graph->edge_count);
    printf("[node] (edge, cost)\n\n");

    struct node_t *node_i;
    for (int i = 0; i < graph->node_count; i++) {
        printf("[%d] ", i);
        node_i = graph->nodes[i];
        while (node_i != NULL) {
            printf("(%d - %d) ", node_i->index, node_i->cost);
            node_i = node_i->next;
        }
        putchar('\n');
    }

}

void shortest_path_print(struct shortest_path *sp, int node_count)
{
    printf("[node]  cost\t\t previous node.\n");
    printf("-----------------------------------\n");
    for (int i = 0; i < node_count; i++) {
        printf("[%d]\t", i);
        if (sp[i].total_cost == INF) {
            printf("UNREACHABLE\n");
        } else {
            printf("%d\t\t", sp[i].total_cost);
            if (sp[i].previous_index != -1)
                printf("%d\n", sp[i].previous_index);
            else
                printf("STARTING NODE\n");
        }
    }
}

/* dijkstra's shortest path from a to all other nodes in the graph */
struct shortest_path *dijkstra(struct graph_t *graph, int start_node)
{
    bool visited[graph->node_count];
    memset(visited, false, graph->node_count);
    struct heapq_t *hq = heapq_malloc();
    struct shortest_path *sp = malloc(sizeof(struct shortest_path) * graph->node_count);
    /* set initial cost to be as large as possible */
    for (int i = 0; i < graph->node_count; i++)
        sp[i].total_cost = INF;

    sp[start_node].total_cost = 0;
    sp[start_node].previous_index = -1;
    heapq_push(hq, start_node, 0);

    struct node_t *neighbour;
    struct node_t *node;
    while ((node = heapq_pop(hq)) != NULL) {
        visited[node->index] = true;
        neighbour = graph->nodes[node->index];

        /* check all connected neighbours of the current node */
        while (neighbour != NULL) {
            /* if a node is already visited, we can't find a shorter path */
            if (visited[neighbour->index]) {
                neighbour = neighbour->next;
                continue;
            }
            int new_cost = sp[node->index].total_cost + neighbour->cost;
            /* update shortest path is newly calculated cost is less than previously calcualted */
            if (new_cost < sp[neighbour->index].total_cost) {
                sp[neighbour->index].previous_index = node->index;
                sp[neighbour->index].total_cost = new_cost;
                heapq_push(hq, neighbour->index, new_cost);
            }
            neighbour = neighbour->next;
        }
        /* 
         * why free? heapq_pop() returns a malloced node that would otherwise be lost if it was not
         * freed here 
         */
        free(node);
    }

    heapq_free(hq);
    return sp;
}

void do_dijkstra(char *file_name, int starting_node)
{
    struct graph_t graph;
    struct shortest_path *sp;
    bool err = parse_file_into_graph(file_name, &graph);
    if (err) {
        fprintf(stderr, "error: could not read file '%s'\n", file_name);
        exit(1);
    }

    sp = dijkstra(&graph, starting_node);
    shortest_path_print(sp, graph.node_count);
    graph_free(&graph);
    free(sp);
}

int main(int argc, char **argv)
{
    if (argc != 3) {
        fprintf(stderr, "usage: first argument should be path to input file, and second should be\
starting node\n");
        fprintf(stderr, "example: dijkstra vg1 1");
        exit(1);
    }

    int starting_node = atoi(argv[2]);
    printf("Dijkstra's algorithm on file '%s' using %d as starting node.\n\n", argv[1],
           starting_node);
    do_dijkstra(argv[1], starting_node);
}
