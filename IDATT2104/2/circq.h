/* Written by Nicolai H. Brand 2022-2023 */
#ifndef CIRCQ_H
#define CIRCQ_H

#include <stdlib.h>
#include <stdbool.h>

#define QUEUE_START_CAP 32

typedef struct circular_queue_t {
    void **elements;
    size_t start, end, size, capacity;
} Circq;


Circq *circq_alloc(size_t starting_capacity);

void circq_free(Circq *q);

bool circq_enqueue(Circq *q, void *e);
#define enqueue(q, e) cirq_enqueue(q, e)

void *circq_dequeue(Circq *q);
#define dequeue(q, e) cirq_dequeue(q, e)

bool circq_empty(Circq *q);

#endif /* CIRCQ_H */

