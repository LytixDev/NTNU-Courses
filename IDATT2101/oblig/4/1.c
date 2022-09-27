/* Written by Nicolai H. Brand, Brage H. Kvamme 2022 */
#include <stdlib.h>     // malloc, free
#include <stdint.h>     // int8_t type
#include <stdio.h>      // fprintf, printf
#include <stdbool.h>    // bool type


typedef int8_t i8;
typedef struct node_t Node;
typedef struct doubly_linked_list_t DLinkedList;

struct node_t {
    i8 value;
    Node *next;
    Node *prev;
};

struct doubly_linked_list_t {
    Node *head;
    Node *tail;
    size_t size;
};


Node *node_malloc(i8 value)
{
    Node *n = malloc(sizeof(Node));
    n->value = value;
    return n;
}

void linked_list_free(DLinkedList *l)
{
    if (l->head == NULL) {
        free(l);
        return;
    }

    Node *n = l->head, *next = n->next;

    do {
        free(n);
        n = next;
        if (next != NULL)
            next = next->next;
    } while (n != NULL);

    free(l);
}

void linked_list_append(DLinkedList *l, i8 value)
{
    Node *n = node_malloc(value);

    if (l->size == 0) {
        l->tail = n;
    } else {
        /* point old tail to new tail */
        l->tail->next = n;
        n->prev = l->tail;
        /* update the DLinkedList's reference to the new tail */
        l->tail = n;
    }
    l->size++;
}

void linked_list_prepend(DLinkedList *l, i8 value)
{
    Node *n = node_malloc(value);

    if (l->size == 0) {
        l->head = n;
    } else {
        /* point old head to new head */
        l->head->prev = n;
        n->next = l->head;
        /* update the DLinkedList's reference to the new head */
        l->head = n;
    }
    l->size++;
}

void node_add_head(Node **ref, i8 value)
{
    Node *n = node_malloc(value);
    (*ref)->prev = n;
    n->next = *ref;
    *ref = n;
}

DLinkedList *parse(char *str)
{
    char c;
    DLinkedList *l = malloc(sizeof(DLinkedList));

    while ((c = *str++) != 0)
        linked_list_append(l, (i8)c - '0');

    return l;
}

void print(DLinkedList *l)
{
    Node *n = l->head;

    for (Node *n = l->head; n != NULL; n = n->next)
        printf("%d", n->value);

    putchar('\n');
}

static void loan_and_update(Node *n)
{
    Node *prev = n->prev;
    if (prev == NULL) {
        fprintf(stderr, "Program does not support a smaller number being subtracted a larger number\n");
        exit(1);
    }

    if (prev->value > 0) {
        prev->value--;
        n->value += 10;
    } else {
        /* grab next available digit and update the chain, recursively */
        loan_and_update(prev);
        loan_and_update(n);
    }
}

DLinkedList *subtract(DLinkedList *a, DLinkedList *b)
{
    i8 sum;
    DLinkedList *res = malloc(sizeof(DLinkedList));
    Node *a_ref = a->tail, *b_ref = b->tail;

    while (!(a_ref == NULL && b_ref == NULL)) {
        /* a has no more digits, but b has, meaning b > a */
        if (a_ref == NULL) {
            fprintf(stderr, "Program does not support a smaller number being subtracted a larger number\n");
            exit(1);
        }

        sum = a_ref->value;
        if (b_ref != NULL)
            sum -= b_ref->value;

        if (sum < 0) {
            loan_and_update(a_ref);
            sum += 10;
        }

        linked_list_prepend(res, sum);

        /* step the chain */
        a_ref = a_ref->prev;
        if (b_ref != NULL)
            b_ref = b_ref->prev;
    }

    return res;
}

DLinkedList *add(DLinkedList *a, DLinkedList *b)
{
    i8 sum;
    bool carry = false;
    DLinkedList *res = malloc(sizeof(DLinkedList));
    Node *a_ref = a->tail, *b_ref = b->tail;

    /* break when all digits have been added */
    while (!(a_ref == NULL && b_ref == NULL)) {
        sum = 0;
        if (a_ref != NULL)
            sum += a_ref->value;
        if (b_ref != NULL)
            sum += b_ref->value;
        sum += (int)carry;


        if (sum > 9) {
            linked_list_prepend(res, sum - 10);
            carry = true;
        } else {
            linked_list_prepend(res, sum);
            carry = false;
        }

        if (a_ref != NULL)
            a_ref = a_ref->prev;
        if (b_ref != NULL)
            b_ref = b_ref->prev;
    }

    if (carry)
        linked_list_prepend(res, 1);

    return res;
}

int main(int argc, char *argv[])
{
    /*
     * command line input should be on the form of:
     * int (+ | -) int
     * example:
     * 999 + 1
     * or:
     * 300 - 1
     */
    if (argc != 4) {
        fprintf(stderr, "Expected 3 arguments, got %d\n", argc - 1);
        return 1;
    }

    DLinkedList *a = parse(argv[1]);
    DLinkedList *b = parse(argv[3]);
    DLinkedList *result;

    if (argv[2][0] == '+')
        result = add(a, b);
    else
        result = subtract(a, b);

    print(result);

    linked_list_free(a);
    linked_list_free(b);
    linked_list_free(result);
}
