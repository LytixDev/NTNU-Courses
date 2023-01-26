/* Written by Nicolai H. Brand 2022-2023 */
#include <stdlib.h>
#include <stdbool.h>
#include "circq.h"


Circq *circq_alloc(size_t starting_capacity)
{
    Circq *q = malloc(sizeof(Circq));
    q->elements = malloc(starting_capacity * sizeof(void *));
    q->capacity = starting_capacity;
    q->start = q->end = q->size = 0;
    return q;
}

void circq_free(Circq *q)
{
    free(q->elements);
    free(q);
}

bool circq_enqueue(Circq *q, void *e)
{
    if (q->size == q->capacity)
        return false;

    q->elements[q->end] = e;
    q->end = (q->end + 1) % q->capacity;
    q->size++;
    return true;
}

void *circq_dequeue(Circq *q)
{
    if (q->size == 0)
        return NULL;

    void *e = q->elements[q->start];
    q->start = (q->start + 1) % q->capacity;
    q->size--;

    return e;
}

bool circq_empty(Circq *q)
{
    return q->size == 0;
}
