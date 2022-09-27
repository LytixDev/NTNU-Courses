/* Written by Nicolai H. Brand 2022 */
#include <stdlib.h>     // malloc, free, realloc
#include <string.h>     // strlen, strcmp
#include <stdio.h>      // fgets, printf

#define QUEUE_START_CAP 32


typedef struct node_t Node;

struct node_t {
    char *data;
    Node *left;
    Node *right;
};

typedef struct queue_t {
    void **elements;
    size_t start;
    size_t end;
    size_t size;
    size_t capacity;
} Queue;


Queue *queue_malloc(size_t starting_capacity)
{
    Queue *q = malloc(sizeof(Queue));
    q->elements = malloc(starting_capacity * sizeof(void *));
    q->capacity = starting_capacity;
    q->start = q->end = q->size = 0;
    return q;
}

void queue_free(Queue *q)
{
    free(q->elements);
    free(q);
}

void queue_append(Queue *q, void *e)
{
    if (q->size == q->capacity)
        return;

    q->elements[q->end] = e;
    /* circular queue */
    q->end = (q->end + 1) % q->capacity;
    q->size++;
}

void *queue_next(Queue *q)
{
    if (q->size == 0)
        return NULL;

    void *e = q->elements[q->start];
    q->start = (q->start + 1) % q->capacity;
    q->size++;

    return e;
}


Node *node_malloc(char str[])
{
    Node *n = malloc(sizeof(Node));
    n->data = malloc((strlen(str) + 1) * sizeof(char));
    strcpy(n->data, str);
    return n;
}

void node_descent_free(Node *root)
{
    if (root->left != NULL)
        node_descent_free(root->left);
    if (root->right != NULL)
        node_descent_free(root->right);

    free(root->data);
    free(root);
}

void bst_insert(Node **root, char *str)
{
    if (*root == NULL) {
        *root = node_malloc(str);
        return;
    }

    int cmp = strcmp(str, (*root)->data);
    if (cmp > 0)
        return bst_insert(&(*root)->right, str);

    return bst_insert(&(*root)->left, str);
}

void level_order_traversal(Node *root)
{
    Queue *q = queue_malloc(QUEUE_START_CAP);

    size_t level = 1, items = 1;
    int spacing = 32;
    for (Node *n = root; n != NULL; n = (Node *)queue_next(q), items++) {
        if (level == 5)
            break;

        if (n->left != NULL)
            queue_append(q, n->left);
        if (n->right != NULL)
            queue_append(q, n->right);

        printf("%*.s%s ", spacing, "", n->data);
        if (items == level) {
            items = 0;
            level++;
            spacing /= 2;
            putchar('\n');
        }
    }

    putchar('\n');
    queue_free(q);
}

int main()
{
    Node *root;
    char buffer[64];

    while (strcmp(buffer, "exit") != 0) {
        printf(">>> ");
        fgets(buffer, 64, stdin);
        /* remove trailing '\n' */
        buffer[strlen(buffer) - 1] = 0;
        bst_insert(&root, buffer);
        level_order_traversal(root);
    };

    node_descent_free(root);
}
