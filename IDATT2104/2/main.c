/* Written by Nicolai H. Brand 2023 */
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <pthread.h>
#include <unistd.h>

#include "circq.h"
#define DEBUG

typedef void *worker_func(void *);

struct Workers {
    size_t n_workers;
    pthread_t *worker_threads;
    pthread_mutex_t lock;
    pthread_cond_t cv;
    bool *is_running;
    Circq *job_queue;
};

struct Job {
    worker_func *func;
    size_t delay_in_micro_seconds;
};

struct Workers *workers_alloc(size_t n)
{
    struct Workers *w = malloc(sizeof(struct Workers));
    w->n_workers = n;
    w->worker_threads = malloc(sizeof(pthread_t) * n);

    w->job_queue = circq_alloc(32);

    pthread_mutex_init(&w->lock, NULL);
    pthread_cond_init(&w->cv, NULL);
    w->is_running = malloc(sizeof(bool));
    *w->is_running = true;

    return w;
}

void *worker_thread_start(void *p)
{
    /* every worker thread runs this loop */
    struct Workers *w = p;
    struct Job *job;

    while (1) {
        /* grab a job, or, wait until a job is present */
        pthread_mutex_lock(&w->lock);
        while (*w->is_running && circq_empty(w->job_queue))
            pthread_cond_wait(&w->cv, &w->lock);

        job = circq_dequeue(w->job_queue);
        pthread_cond_signal(&w->cv);
        pthread_mutex_unlock(&w->lock);

        /* execute job (if exist) without holding the lock */
        if (!*w->is_running && job == NULL)
            break;

        if (job == NULL)
            continue;

#ifdef DEBUG
        printf("---found job\n");
#endif
        usleep(job->delay_in_micro_seconds);
        job->func(NULL);
        free(job);
    }

    return NULL;
}


void workers_start(struct Workers *w)
{
    for (size_t i = 0; i < w->n_workers; i++) {
#ifdef DEBUG
        printf("---create thread %zu\n", i);
#endif
        pthread_create(&w->worker_threads[i], NULL, worker_thread_start, w);
    }
}

#define workers_post(w, f) workers_post_timeout(w, f, 0)
void workers_post_timeout(struct Workers *w, worker_func *func, int delay_in_micro_seconds)
{
    struct Job *job = malloc(sizeof(struct Job));
    job->func = func;
    job->delay_in_micro_seconds = delay_in_micro_seconds;
    pthread_mutex_lock(&w->lock);
#ifdef DEBUG
    printf("---added job to queue\n");
#endif
    circq_enqueue(w->job_queue, job);
    pthread_mutex_unlock(&w->lock);
}

void workers_join(struct Workers *w)
{
    *w->is_running = false;
    for (size_t i = 0; i < w->n_workers; i++) {
        pthread_join(w->worker_threads[i], NULL);
    }
}

void *dummy(void *args)
{
    printf("JOB: start 3s sleep\n");
    sleep(3);
    printf("JOB: end 3s sleep\n");
    return NULL;
}

void *dummy2(void *args)
{
    printf("JOB: hello\n");
    return NULL;
}

int main()
{
    printf("---start 4 worker threads\n");
    struct Workers *w = workers_alloc(4);
    workers_start(w);
    workers_post_timeout(w, dummy2, 10000);
    workers_post(w, dummy);
    workers_post(w, dummy);
    workers_join(w);

    printf("\n\n---start event loop\n");
    struct Workers *event_loop = workers_alloc(1);
    workers_start(event_loop);
    workers_post_timeout(event_loop, dummy2, 1000);
    workers_post(event_loop, dummy);
    workers_join(event_loop);
}
