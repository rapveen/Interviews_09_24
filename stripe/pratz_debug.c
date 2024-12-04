#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h> // For sleep function

#define MAX_THREADS 4
#define MAX_QUEUE 10

typedef struct {
    void (*function)(void* arg); // Function pointer for the task
    void* arg;                   // Argument for the task
} task_t;

typedef struct {
    pthread_t threads[MAX_THREADS]; // Array of worker threads
    task_t task_queue[MAX_QUEUE];   // Task queue
    int task_count;                  // Current number of tasks
    int stop;                        // Flag to indicate shutdown
    pthread_mutex_t mutex;          // Mutex for synchronizing access
    pthread_cond_t condition;       // Condition variable for signaling
} thread_pool_t;

// Function executed by worker threads
void* worker(void* pool) {
    thread_pool_t* tpool = (thread_pool_t*)pool;

    while (1) {
        pthread_mutex_lock(&tpool->mutex);

        // Wait for tasks or shutdown
        while (tpool->task_count == 0 && !tpool->stop) {
            pthread_cond_wait(&tpool->condition, &tpool->mutex);
        }

        // Check for shutdown
        if (tpool->stop) {
            pthread_mutex_unlock(&tpool->mutex);
            break;
        }

        // Get the next task
        task_t task = tpool->task_queue[--tpool->task_count];

        pthread_mutex_unlock(&tpool->mutex);

        // Execute the task
        task.function(task.arg);
    }
    return NULL;
}

// Initialize the thread pool
void thread_pool_init(thread_pool_t* tpool) {
    tpool->task_count = 0;
    tpool->stop = 0;
    pthread_mutex_init(&tpool->mutex, NULL);
    pthread_cond_init(&tpool->condition, NULL);

    // Create worker threads
    for (int i = 0; i < MAX_THREADS; i++) {
        pthread_create(&tpool->threads[i], NULL, worker, tpool);
    }
}

// Add a task to the thread pool
void thread_pool_add_task(thread_pool_t* tpool, void (*function)(void*), void* arg) {
    pthread_mutex_lock(&tpool->mutex);

    // Add task to the queue
    tpool->task_queue[tpool->task_count].function = function;
    tpool->task_queue[tpool->task_count].arg = arg;
    tpool->task_count++;

    pthread_cond_signal(&tpool->condition);
    pthread_mutex_unlock(&tpool->mutex);
}

// Shutdown the thread pool
void thread_pool_shutdown(thread_pool_t* tpool) {
    pthread_mutex_lock(&tpool->mutex);
    tpool->stop = 1; // Set shutdown flag
    pthread_cond_broadcast(&tpool->condition); // Wake all threads
    pthread_mutex_unlock(&tpool->mutex);

    // Wait for all threads to finish
    for (int i = 0; i < MAX_THREADS; i++) {
        pthread_join(tpool->threads[i], NULL);
    }

    // Clean up
    pthread_mutex_destroy(&tpool->mutex);
    pthread_cond_destroy(&tpool->condition);
}

// Example task function
void example_task(void* arg) {
    int task_id = *((int*)arg);
    printf("Executing task %d\n", task_id);
    sleep(1); // Simulate work
    free(arg); // Free allocated memory
}

int main() {
    thread_pool_t tpool;
    thread_pool_init(&tpool); // Initialize the thread pool

    // Add tasks to the thread pool
    for (int i = 0; i < 10; i++) {
        int* task_id = malloc(sizeof(int)); // Allocate memory for task argument
        *task_id = i; // Set task ID
        thread_pool_add_task(&tpool, example_task, task_id); // Add task
    }

    // Shutdown the thread pool
    thread_pool_shutdown(&tpool);

    return 0; // End of program
}