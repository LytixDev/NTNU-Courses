#include <stdlib.h>
#include <stdio.h>
#include <pthread.h>

#define T int

struct LinkedList {
    struct LinkedListItem *head;
    struct LinkedListItem *tail;
};

struct LinkedListItem {
    T val;
    struct LinkedListItem *next;
};

struct params {
    int upper;
    int lower;
};

int is_prime(int a)
{
    if (a == 2 || a == 3)
        return 1;

    if (a <= 1 || a % 2 == 0 || a % 3 == 0)
        return 0;

    for (int i = 5; i * i <= a; i += 6) {
        if (a % i == 0 || a % (i + 2) == 0)
            return 0;
    }
    return 1;
}

void *find_all_primes(void *arg)
{
    struct params *p = (struct params *)arg;
    int lower = p->lower;
    int upper = p->upper;

    struct LinkedList *ll = malloc(sizeof(struct LinkedList));
    struct LinkedListItem *item = malloc(sizeof(struct LinkedListItem));
    item->val = -1;
    ll->head = item;

    for (int i = lower; i < upper + 1; i++) {
        if (is_prime(i)) {
            if (item->val != -1)
                item = item->next;
            item->val = i;
            item->next = malloc(sizeof(struct LinkedListItem));
        }
    }

    /* no prime number was found */
    if (ll->head->val == -1)
        return NULL;

    item->next = NULL;
    ll->tail = item;
    return ll;
}

int main(int argc, char *argv[])
{
    if (argc != 4) {
        fprintf(stderr, "bad input\nusage: %s lower_bound upper_bound n_threads\n", argv[0]);
        return 1;
    }

    int lower_bound = atoi(argv[1]);
    int upper_bound = atoi(argv[2]);
    int n_threads = atoi(argv[3]);
    int diff = upper_bound - lower_bound;
    int partition = diff / n_threads;

    pthread_t threads[n_threads];

    struct timespec t_start, finish;
    clock_gettime(CLOCK_MONOTONIC_RAW, &t_start);

    for (int i = 0; i < n_threads; i++) {
        struct params *p = malloc(sizeof(struct params));
        p->lower = partition * i + 1;
        p->upper = partition * (i + 1);
        if (i == n_threads - 1)
            p->upper = upper_bound;
        if (i == 0)
            p->lower = lower_bound;
        //printf("%d - %d\n", p->lower, p->upper);
        pthread_create(&threads[i], NULL, find_all_primes, p);
    }

    struct LinkedListItem *tail_tmp;
    struct LinkedListItem *head = NULL;
    void *ret;
    for (int i = 0; i < n_threads; i++) {
        pthread_join(threads[i], &ret);
        if (ret == NULL)
            continue;

        /* join all linked lists together */
        struct LinkedList *ll = ret;
        if (head == NULL)
            head = ll->head;
        else
            tail_tmp->next = ll->head;
        tail_tmp = ll->tail;

    }

    clock_gettime(CLOCK_MONOTONIC_RAW, &finish);
    double elapsed = (finish.tv_sec - t_start.tv_sec);
    elapsed += (finish.tv_nsec - t_start.tv_nsec) / 1000000000.0;

#ifdef PRINT
    printf("All primes in interval [%d, %d] (%f ms)\n", lower_bound, upper_bound, elapsed);
#ifdef PRINT_PRIMES
    while (head != NULL) {
        printf("%d ", head->val);
        head = head->next;
    }
    putchar('\n');
#endif
#endif

    return 0;
}
