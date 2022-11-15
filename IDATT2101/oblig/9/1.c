/* Written by Nicolai H. Brand 2022 */

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <stdarg.h>
#include <assert.h>

#ifdef VERBOSE
#       include <time.h>
#endif

#ifdef _WIN32
#       include <direct.h>
#elif defined __linux__
#       include <sys/stat.h>
#endif


/* function declarations */
struct shortest_path *parse_preprocessed_graph(char *file_name);


/* compares priority of items in heapqueue. E.I: is a > b ? */
typedef bool (compare_func)(void *a, void *b);

/* min-heap queue inspired by: https://docs.python.org/3/library/heapq.html */
struct heapq_t {
    void **items; 
    int size;
    int capacity;
    compare_func *cmp;
};

struct node_t {
    int node_idx;
    double latitude;
    double longitude;
};

/* node info used in the queue when performing dijkstra */
struct node_info {
    int node_idx;
    int cost_from_start_node;
};

/* node info used in the queue when performing A* / ALT */
struct node_info_alt {
    int node_idx;
    int cost_from_start_node;
    double heuristic;
};

struct edge_t {
    /* index/value/identifer of the node */
    int from_idx;
    int to_idx;
    int cost;
    struct edge_t *next;
};

struct graph_t {
    struct node_t **node_list;
    /*
     * every node has a linked list of edges.
     * the value at the index of the neighbour_list is the head of the linked list of edges.
     *
     * example:
     * [0]      -> edge -> edge -> edge
     * [1]      -> edge
     * [2]      -> NULL
     * [3]      -> edge -> edge
     * ...
     * [i]
     */
    struct edge_t **neighbour_list;
    int node_count;
    int edge_count;
};

/* ad-hoc datastructure to store information given by performing dijkstra */
struct shortest_path {
    int previous_idx;
    int total_cost;
};

struct alt_landmarks {
    int n;
    struct shortest_path **shortest_paths;
};

#define VA_NUMBER_OF_ARGS(...) (sizeof((int[]){__VA_ARGS__}) / sizeof(int))

#ifndef PREPROCESSED_DIR
#       define PREPROCESSED_DIR "preprocessed"
#endif

#define HEAPQ_STARTING_CAPACITY 32
#define ERROR_EOF -1
#define INF (1 << 30)

#define NODE_UNVISITED -1
#define NODE_START -2

#define heapq_left_child_idx(parent_idx) (parent_idx * 2 + 1)
#define heapq_right_child_idx(parent_idx) (parent_idx * 2 + 2)
#define heapq_parent_idx(child_idx) ((child_idx - 1) / 2)

#define heapq_has_left(idx, size) (heapq_left_child_idx(idx) < size)
#define heapq_has_right(idx, size) (heapq_right_child_idx(idx) < size)



/* ---------- io functions ---------- */
static int f_next_int(FILE *fp)
{
    char ch;
    char buf[128] = {0};
    int i = 0;

    /* remove all leading whitespace */
    while ((ch = fgetc(fp)))
        if (ch != ' ' && ch != '\t')
            break;

    if (ch == EOF)
        return ERROR_EOF;

    buf[i++] = ch;

    do {
        ch = fgetc(fp);
        if (ch == ' ' || ch == '\t') {
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

static double f_next_double(FILE *fp)
{
    char ch;
    char buf[128] = {0};
    int i = 0;

    /* remove all leading whitespace */
    while ((ch = fgetc(fp)))
        if (ch != ' ' && ch != '\t')
            break;

    if (ch == EOF)
        return ERROR_EOF;

    buf[i++] = ch;

    do {
        ch = fgetc(fp);
        if (ch == ' ' || ch == '\t') {
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

    return strtod(buf, NULL);
}

void f_consume_line(FILE *fp)
{
    char ch;
    /* consume entire line */
    while ((ch = fgetc(fp))) {
        if (ch == '\n')
            break;
        if (ch == EOF)
            break;
    }
}


/* ---------- heap functions ---------- */
static inline void *heapq_left_child(struct heapq_t *hq, int idx)
{
    return hq->items[heapq_left_child_idx(idx)];
}

static inline void *heapq_right_child(struct heapq_t *hq, int idx)
{
    return hq->items[heapq_right_child_idx(idx)];
}

static inline void *heapq_parent(struct heapq_t *hq, int idx)
{
    return hq->items[heapq_parent_idx(idx)];
}

static void swap(struct heapq_t *hq, int a, int b)
{
    void *tmp = hq->items[a];
    hq->items[a] = hq->items[b];
    hq->items[b] = tmp;
}

static void ensure_capacity(struct heapq_t *hq)
{
    if (hq->size == hq->capacity) {
        hq->capacity *= 2;
        hq->items = realloc(hq->items, hq->capacity * sizeof(void *));
    }
}

static void heapify_up(struct heapq_t *hq)
{
    int idx = hq->size - 1;
    int parent_idx = heapq_parent_idx(idx);
    /* keep "repearing" heap as long as parent is greater than child */
    while (parent_idx >= 0 && hq->cmp(hq->items[parent_idx], hq->items[idx])) {
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
        if (heapq_has_right(idx, hq->size) && hq->cmp(hq->items[min_idx], 
                                                      heapq_right_child(hq, idx)))
            min_idx = heapq_right_child_idx(idx);

        if (hq->cmp(hq->items[min_idx], hq->items[idx])) {
            break;
        } else {
            swap(hq, idx, min_idx);
            idx = min_idx;
        }
    }
}

void *heapq_get(struct heapq_t *hq, int idx)
{
    if (idx < 0 || idx >= hq->size)
        return NULL;

    return hq->items[idx];
}

void *heapq_pop(struct heapq_t *hq)
{
    struct node_t *item = heapq_get(hq, 0);
    if (item == NULL)
        return NULL;

    hq->items[0] = hq->items[--hq->size];
    heapify_down(hq);
    return item;
}

void heapq_push(struct heapq_t *hq, void *item)
{
    ensure_capacity(hq);
    hq->items[hq->size++] = item;
    heapify_up(hq);
}

void heapq_push_node(struct heapq_t *hq, int node, int cost_from_start_node)
{
    struct node_info *ni = malloc(sizeof(struct node_info));
    ni->node_idx = node;
    ni->cost_from_start_node = cost_from_start_node;
    heapq_push(hq, ni);
}

void heapq_push_node_alt(struct heapq_t *hq, int node, int cost_from_start_node, double heuristic)
{
    struct node_info_alt *ni = malloc(sizeof(struct node_info_alt));
    ni->node_idx = node;
    ni->cost_from_start_node = cost_from_start_node;
    ni->heuristic = heuristic;
    heapq_push(hq, ni);
}

static void heapq_free_element(struct heapq_t *hq, int idx)
{
    if (heapq_has_left(idx, hq->size))
        heapq_free_element(hq, heapq_left_child_idx(idx));

    if (heapq_has_right(idx, hq->size))
        heapq_free_element(hq, heapq_right_child_idx(idx));

    free(hq->items[idx]);
}

void heapq_free(struct heapq_t *hq)
{
    if (hq->size != 0) {
        heapify_down(hq);
        heapq_free_element(hq, 0);
    }
    free(hq->items);
    free(hq);
}

struct heapq_t *heapq_malloc(compare_func *cmp)
{
    struct heapq_t *hq = malloc(sizeof(struct heapq_t));
    hq->size = 0;
    hq->capacity = HEAPQ_STARTING_CAPACITY;
    hq->items = malloc(HEAPQ_STARTING_CAPACITY * sizeof(void *));
    hq->cmp = cmp;
    return hq;
}

bool compare(void *a, void *b)
{
    assert(a != NULL && b != NULL);
    return ((struct node_info *)a)->cost_from_start_node >
           ((struct node_info *)b)->cost_from_start_node;
}

bool compare_alt(void *a, void *b)
{
    assert(a != NULL && b != NULL);
    struct node_info_alt *A = a;
    struct node_info_alt *B = b;
    return A->heuristic + A->cost_from_start_node > B->heuristic + B->cost_from_start_node;
}


/* ---------- graph functions ---------- */
void graph_print(struct graph_t *graph)
{
    printf("total nodes: %d, total edges: %d.\n", graph->node_count, graph->edge_count);
    printf("[node] list of all edges (to, cost).\n");

    struct edge_t *edge_i;
    for (int i = 0; i < graph->node_count; i++) {
        printf("[%d] ", i);
        edge_i = graph->neighbour_list[i];
        while (edge_i != NULL) {
            printf("(%d, %d) ", edge_i->to_idx, edge_i->cost);
            edge_i = edge_i->next;
        }
        putchar('\n');
    }
    putchar('\n');

}

void graph_insert_node(struct graph_t *graph, int node_idx, double latitude, double longitude)
{
    struct node_t *node = malloc(sizeof(struct node_t));
    node->node_idx = node_idx;
    node->latitude = latitude;
    node->longitude = longitude;
    graph->node_list[node_idx] = node;
}

static void graph_insert_edge(struct graph_t *graph, int from_idx, int to_idx, int cost)
{
    struct edge_t *edge = malloc(sizeof(struct edge_t));
    edge->from_idx = from_idx;
    edge->to_idx = to_idx;
    edge->cost = cost;
    edge->next = NULL;

    struct edge_t *head = graph->neighbour_list[from_idx];
    if (head == NULL) {
        graph->neighbour_list[from_idx] = edge;
        return;
    }

    /* use new node as linkedlist head */
    edge->next = head;
    graph->neighbour_list[from_idx] = edge;
}

static struct graph_t *graph_transpose(struct graph_t *original)
{
    struct graph_t *transposed = malloc(sizeof(struct graph_t));
    transposed->node_list = NULL;
    transposed->neighbour_list = malloc(original->node_count * sizeof(struct edge_t *));
    for (int i = 0; i < original->node_count; i++)
        transposed->neighbour_list[i] = NULL;

    transposed->node_count = original->node_count;
    transposed->edge_count = original->edge_count;

    /* reverse all connections */
    for (int node = 0; node < transposed->node_count; node++) {
        struct edge_t *goes_to = original->neighbour_list[node];
        while (goes_to != NULL) {
            graph_insert_edge(transposed, goes_to->to_idx, goes_to->from_idx, 
                              goes_to->cost);
            goes_to = goes_to->next;
        }
    }
    return transposed;
}

bool parse_node_file(char *file_name, struct graph_t *graph)
{
    FILE *fp;
    fp = fopen(file_name, "r");
    if (fp == NULL)
        return true;

    int node_count = f_next_int(fp);
    if (node_count == ERROR_EOF || node_count < 1)
        return true;

    graph->node_count = node_count;
    graph->node_list = malloc(node_count * sizeof(struct node_t *));

    while (true) {
        int node_idx = f_next_int(fp);
        double latitude = f_next_double(fp);
        double longitude = f_next_double(fp);
        if (node_idx == ERROR_EOF || latitude == ERROR_EOF || longitude == ERROR_EOF)
            break;
        
        graph_insert_node(graph, node_idx, latitude, longitude);
    }

    fclose(fp);
    return false;
}

bool parse_edge_file(char *file_name, struct graph_t *graph)
{
    FILE *fp;
    fp = fopen(file_name, "r");
    if (fp == NULL)
        return true;

    int edge_count = f_next_int(fp);
    if (edge_count == ERROR_EOF || edge_count < 1)
        return true;

    graph->edge_count = edge_count;
    assert(graph->node_count > 0);
    graph->neighbour_list = malloc(graph->node_count * sizeof(struct edge_t *));
    for (int i = 0; i < graph->node_count; i++)
        graph->neighbour_list[i] = NULL;

    while (true) {
        int from_idx = f_next_int(fp);
        int to_idx = f_next_int(fp);
        int cost = f_next_int(fp);
        f_consume_line(fp);
        if (to_idx == ERROR_EOF || from_idx == ERROR_EOF || cost == ERROR_EOF)
            break;

        graph_insert_edge(graph, from_idx, to_idx, cost);
    }

    fclose(fp);
    return false;
}

void graph_free_neighbours(struct graph_t *graph)
{
    struct edge_t *edge_i, *prev;
    for (int i = 0; i < graph->node_count; i++) {
        edge_i = graph->neighbour_list[i];
        while (edge_i != NULL) {
            prev = edge_i;
            edge_i = edge_i->next;
            free(prev);
        }
    }
}

struct graph_t *graph_parse(char *node_file, char *edge_file)
{
    struct graph_t *graph = malloc(sizeof(struct graph_t));
    bool err;
    err = parse_node_file(node_file, graph);
    if (err){
        fprintf(stderr, "could not parse node file: %s\n", node_file);
        exit(1);
    }
    err = parse_edge_file(edge_file, graph);
    if (err) {
        fprintf(stderr, "could not parse edge file: %s\n", edge_file);
        exit(1);
    }

    return graph;
}

void graph_free(struct graph_t *graph)
{
    graph_free_neighbours(graph);
    free(graph->neighbour_list);
    if (graph->node_list != NULL) {
        for (int i = 0; i < graph->node_count; i++)
            free(graph->node_list[i]);

        free(graph->node_list);
    }

    free(graph);
}

void shortest_path_print(struct shortest_path *sp, int node_count)
{
    printf("[node] [previous node] [cost]\n");
    printf("-----------------------------------\n");
    for (int i = 0; i < node_count; i++) {
        printf("%d\t", i);
        if (sp[i].previous_idx == NODE_START) {
            printf("start\n");
            continue;
        } else if (sp[i].previous_idx == NODE_UNVISITED) {
            printf("unreachable\n");
            continue;
        } else {
            printf("%d\t", sp[i].previous_idx);
        }

        if (sp[i].total_cost == INF)
            printf("ERROR: visited node has total_cost set to infinity\n");
        else
            printf("%d\n", sp[i].total_cost);
    }
}


/* ---------- ALT functions ---------- */
struct alt_landmarks *alt_landmarks_malloc(int n, char **file_names)
{
    struct alt_landmarks *landmarks = malloc(sizeof(struct alt_landmarks));
    landmarks->shortest_paths = malloc(n * sizeof(struct shortest_path));
    landmarks->n = n;

    for (int i = 0; i < n; i++) {
        landmarks->shortest_paths[i] = parse_preprocessed_graph(file_names[i]);
    }

    return landmarks;
}

void alt_landmarks_free(struct alt_landmarks *landmarks)
{
    for (int i = 0; i < landmarks->n; i++)
        free(landmarks->shortest_paths[i]);

    free(landmarks->shortest_paths);
    free(landmarks);
}


static double alt_best_heuristic(int current_node_idx, int end_node_idx, struct alt_landmarks
                                 *landmarks)
{
    struct node_t *landmark;
    double best = 0;
    double candidate;

    for (int i = 0; i < landmarks->n; i += 2) {
        /* dist(L_n, end) - dist(L_n, current) */
        candidate = landmarks->shortest_paths[i][end_node_idx].total_cost -
                    landmarks->shortest_paths[i][current_node_idx].total_cost;
        if (candidate > best)
            best = candidate;

        /* dist(current, L_n) - dist(end, L_n) */
        candidate = landmarks->shortest_paths[i + 1][current_node_idx].total_cost -
                    landmarks->shortest_paths[i + 1][end_node_idx].total_cost;
        if (candidate > best)
            best = candidate;
    }

    return best;
}

struct shortest_path *alt(struct graph_t *graph, struct alt_landmarks *landmarks,
                          int start_node_idx, int end_node_idx)
{
#ifdef VERBOSE
    int nodes_visited = 0;
#endif
    if (graph->node_list[end_node_idx] == NULL) {
        fprintf(stderr, "end node: %d not a valid node in graph.", end_node_idx);
        return NULL;
    }

    if (graph->node_list[start_node_idx] == NULL) {
        fprintf(stderr, "start node: %d not a valid node in graph.", end_node_idx);
        return NULL;
    }

    struct node_t *visiting_node = graph->node_list[start_node_idx];
    struct node_t *end_node = graph->node_list[end_node_idx];
    double end_latitude = end_node->latitude;
    double end_longitude = end_node->longitude;

    bool visited[graph->node_count];
    memset(visited, false, sizeof(bool) * graph->node_count);
    struct heapq_t *hq = heapq_malloc(compare_alt);
    struct shortest_path *sp = malloc(sizeof(struct shortest_path) * graph->node_count);
    /* set initial cost to be as large as possible */
    for (int i = 0; i < graph->node_count; i++) {
        sp[i].total_cost = INF;
        sp[i].previous_idx = NODE_UNVISITED;
    }

    sp[start_node_idx].total_cost = 0;
    sp[start_node_idx].previous_idx = NODE_START;
    heapq_push_node_alt(hq, start_node_idx, 0, alt_best_heuristic(start_node_idx, end_node_idx,
                                                                  landmarks));

    struct edge_t *neighbour;
    struct node_info_alt *ni;
    while ((ni = (struct node_info_alt *)heapq_pop(hq)) != NULL) {
#ifdef VERBOSE
        nodes_visited++;
#endif
        visiting_node = graph->node_list[ni->node_idx];
        assert(visiting_node != NULL);
#ifdef PRINT_RUN
        printf("%f,%f\n", visiting_node->latitude, visiting_node->longitude);
#endif

        if (ni->node_idx == end_node_idx)
            break;

        visited[ni->node_idx] = true;
        neighbour = graph->neighbour_list[ni->node_idx];

        /* check all connected neighbours of the current node */
        while (neighbour != NULL) {
            /* if a node is already visited, we can't find a shorter path */
            if (visited[neighbour->to_idx]) {
                neighbour = neighbour->next;
                continue;
            }
            int new_cost = sp[ni->node_idx].total_cost + neighbour->cost;
            int heuristic = alt_best_heuristic(ni->node_idx, end_node_idx, landmarks);

            /* update shortest path if newly calculated cost is less than previously calcualted */
            if (new_cost < sp[neighbour->to_idx].total_cost) {
                sp[neighbour->to_idx].previous_idx = ni->node_idx;
                sp[neighbour->to_idx].total_cost = new_cost;
                heapq_push_node_alt(hq, neighbour->to_idx, new_cost, heuristic);
            }
            neighbour = neighbour->next;
        }
        /* 
         * why free? heapq_pop() returns a malloced node that would otherwise be lost if it was not
         * freed here 
         */
        free(ni);
    }

#ifdef VERBOSE
    printf("ALT algorithm visited %d nodes to find the shortest path between %d and %d\n",
           nodes_visited, start_node_idx, end_node_idx);
#endif

    heapq_free(hq);
    return sp;
}

void do_alt(char *node_file, char *edge_file, int starting_node_idx, int end_node_idx,
            struct alt_landmarks *landmarks)
{
    struct graph_t *graph = graph_parse(node_file, edge_file);

#ifdef VERBOSE
    printf("ALT algorithm on node file '%s' and edge file '%s' using %d as starting node\
\n\n", node_file, edge_file, starting_node_idx);
    clock_t t;
    t = clock();
#endif
    struct shortest_path *sp = alt(graph, landmarks, starting_node_idx, end_node_idx);
#ifdef VERBOSE
    t = clock() - t;
    printf("ALT took: %f seconds to finish.\n", ((double)t)/CLOCKS_PER_SEC);
    printf("Shortest path: %d.\n", sp[end_node_idx].total_cost);
#endif
    graph_free(graph);
    free(sp);
}

/* ---------- dijsktra implementation ---------- */
/* dijkstra's shortest path from a to all other nodes in the graph */
struct shortest_path *dijkstra(struct graph_t *graph, int start_node_idx, int end_node_idx)
{
#ifdef VERBOSE
    int nodes_visited = 0;
#endif
    bool visited[graph->node_count];
    memset(visited, false, sizeof(bool) * graph->node_count);
    struct heapq_t *hq = heapq_malloc(compare);
    struct shortest_path *sp = malloc(sizeof(struct shortest_path) * graph->node_count);
    /* set initial cost to be as large as possible */
    for (int i = 0; i < graph->node_count; i++) {
        sp[i].total_cost = INF;
        sp[i].previous_idx = NODE_UNVISITED;
    }

    sp[start_node_idx].total_cost = 0;
    sp[start_node_idx].previous_idx = NODE_START;
    heapq_push_node(hq, start_node_idx, 0);

    struct edge_t *neighbour;
    struct node_info *ni;
    while ((ni = (struct node_info *)heapq_pop(hq)) != NULL) {
#ifdef VERBOSE
        nodes_visited++;
#endif
#ifdef PRINT_RUN
        struct node_t *visiting_node = graph->node_list[ni->node_idx];
        assert(visiting_node != NULL);
        printf("%f,%f\n", visiting_node->latitude, visiting_node->longitude);
#endif

        if (ni->node_idx == end_node_idx)
            break;

        visited[ni->node_idx] = true;
        neighbour = graph->neighbour_list[ni->node_idx];

        /* check all connected neighbours of the current node */
        while (neighbour != NULL) {
            /* if a node is already visited, we can't find a shorter path */
            if (visited[neighbour->to_idx]) {
                neighbour = neighbour->next;
                continue;
            }
            int new_cost = sp[ni->node_idx].total_cost + neighbour->cost;
            /* update shortest path is newly calculated cost is less than previously calcualted */
            if (new_cost < sp[neighbour->to_idx].total_cost) {
                sp[neighbour->to_idx].previous_idx = ni->node_idx;
                sp[neighbour->to_idx].total_cost = new_cost;
                heapq_push_node(hq, neighbour->to_idx, new_cost);
            }
            neighbour = neighbour->next;
        }
        /* 
         * why free? heapq_pop() returns a malloced node that would otherwise be lost if it was not
         * freed here 
         */
        free(ni);
    }

#ifdef VERBOSE
    printf("Dijkstra's algorithm visited %d nodes to find the shortest path between %d and %d\n",
           nodes_visited, start_node_idx, end_node_idx);
#endif

    heapq_free(hq);
    return sp;
}

void do_dijkstra(char *node_file, char *edge_file, int starting_node, int end_node)
{
    struct graph_t *graph = graph_parse(node_file, edge_file);

#ifdef VERBOSE
    printf("Dijkstra's algorithm on node file '%s' and edge file '%s' using %d as starting node\
\n\n", node_file, edge_file, starting_node);
    clock_t t;
    t = clock();
#endif
    struct shortest_path *sp = dijkstra(graph, starting_node, end_node);
#ifdef VERBOSE
    t = clock() - t;
    printf("Dijkstra took: %f seconds to finish.\n", ((double)t)/CLOCKS_PER_SEC);
    printf("Shortest path: %d.\n", sp[end_node].total_cost);
#endif
    graph_free(graph);
    free(sp);
}

/* ---------- preprocessing functions ---------- */
struct shortest_path *parse_preprocessed_graph(char *file_name)
{
    FILE *fp;
    int fd;
    size_t fsize;

    fp = fopen(file_name, "rb");
    if (fp == NULL) {
        fprintf(stderr, "could not open() file '%s'\n", file_name);
        exit(1);
    }
    fd = fileno(fp);

    /* get the size of the file in O(1) */
    struct stat st;
    fstat(fd, &st);
    fsize = st.st_size / sizeof(int);

    struct shortest_path *sp = calloc(fsize, sizeof(struct shortest_path));
    int d;
    for (size_t i = 0; i < fsize; i++) {
        fread(&d, sizeof(int), 1, fp);
        sp[i].total_cost = d;
    }

    return sp;
}

void preprocess_graph(struct graph_t *graph, char *out_file, int node)
{
    struct shortest_path *sp = dijkstra(graph, node, -1);
    FILE *fp = fopen(out_file, "wb");
    if (fp == NULL) {
        fprintf(stderr, "could not open file '%s' for writing :-(", out_file);
        exit(1);
    }

    for (int i = 0; i < graph->node_count; i++)
        fwrite(&sp[i].total_cost, sizeof(int), 1, fp);

    fclose(fp);
}

#define preprocess(a, b, ...) preprocess_multiple(a, b, VA_NUMBER_OF_ARGS(__VA_ARGS__),\
                                                    __VA_ARGS__)
void preprocess_multiple(char *node_file, char *edge_file, unsigned int n, ...)
{
    struct graph_t *graph = graph_parse(node_file, edge_file);
    struct graph_t *transposed = graph_transpose(graph);
    char file_name[512];
    int node_idx;

    va_list args;
    va_start(args, n);

    for (int i = 0; i < n; i++) {
        node_idx = va_arg(args, int);
        snprintf(file_name, 512, "%s/%d_reversed", PREPROCESSED_DIR, node_idx);
        preprocess_graph(transposed, file_name, node_idx);
        snprintf(file_name, 512, "%s/%d", PREPROCESSED_DIR, node_idx);
        preprocess_graph(graph, file_name, node_idx);
    }

    graph_free(graph);
    graph_free(transposed);

    va_end(args);
}

void alt_vs_dijkstra(char *node_file, char *edge_file, int start_node_idx, int end_node_idx)
{
    //preprocess(node_file, edge_file, 412001, 7425499);
#ifdef VERBOSE
    printf("Finsihed preprocessing graph\n");
#endif
    
    char *file_names[] = {"preprocessed/412001",
                          "preprocessed/412001_reversed",
                          "preprocessed/7425499",
                          "preprocessed/7425499_reversed"};
    struct alt_landmarks *landmarks = alt_landmarks_malloc(4, file_names);

    do_alt(node_file, edge_file, start_node_idx, end_node_idx, landmarks);
    do_dijkstra(node_file, edge_file, start_node_idx, end_node_idx);

    alt_landmarks_free(landmarks);
}

int main(int argc, char **argv)
{
#ifdef _WIN32
    printf("Program depends on sys/stat.h which is only available on UNIX-like systems.\
I can not verify that the program properly works on windows.");
#endif

    alt_vs_dijkstra("data/norden_noder.txt", "data/norden_kanter.txt", 5446653, 6199669);

    return 0;
    if (argc != 5) {
        fprintf(stderr, "usage: %s <path-to-node_file> <path-to-edge-file> <start_node> <end-nod>\n"
                , argv[0]);
        fprintf(stderr, "example: %s noder.txt kanter.txt 1 100\n", argv[0]);
        exit(1);
    } else {
        char *node_file = argv[1];
        char *edge_file = argv[2];
        int start_node = atoi(argv[3]);
        int end_node = atoi(argv[4]);
        do_dijkstra(node_file, edge_file, start_node, end_node);
    }
}
