#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>

// Shared variables for Peterson's algorithm
int flag[2] = {0, 0}; // flag array for producer and consumer
int turn = 0;         // variable to indicate whose turn it is

// Shared buffer and related variables
#define BUFFER_SIZE 5
int buffer[BUFFER_SIZE];
int count = 0; // Number of items in buffer

// Function to simulate producing an item
int produce_item() {
    return rand() % 100; // Produces a random item
}

// Function to simulate consuming an item
void consume_item(int item) {
    printf("Consumer consumed: %d\n", item);
}

// Producer function using Peterson's Algorithm
void *producer(void *arg) {
    while (1) {
        // Produce an item
        int item = produce_item();
        printf("Producer produced: %d\n", item);

        // Peterson's Algorithm: Entry section
        // Producer wants to enter critical section
        flag[0] = 1; 
         // Give turn to consumer
        turn = 1;   
        // Wait if consumer is in critical section 
        while (flag[1] == 1 && turn == 1); 

        // Critical Section: Add item to buffer if there's space
        if (count < BUFFER_SIZE) {
            buffer[count++] = item;
            printf("Buffer size after producing: %d\n", count);
        } else {
            printf("Buffer full! Producer is waiting.\n");
        }

        // Exit Section
        // Producer exits critical section
        flag[0] = 0; 
        // Slow down for demonstration
        sleep(1); 
    }
    return NULL;
}

// Consumer function using Peterson's Algorithm
void *consumer(void *arg) {
    while (1) {
        // Peterson's Algorithm: Entry section
        // Consumer wants to enter critical section
        flag[1] = 1; 
        // Give turn to producer
        turn = 0;    
        // Wait if producer is in critical section
        while (flag[0] == 1 && turn == 0); 

        // Critical Section: Consume item from buffer if available
        if (count > 0) {
            int item = buffer[--count];
            consume_item(item);
            printf("Buffer size after consuming: %d\n", count);
        } else {
            printf("Buffer empty! Consumer is waiting.\n");
        }

        // Exit Section
        // Consumer exits critical section
        flag[1] = 0; 

        // Slow down for demonstration
        sleep(1); 
    }
    return NULL;
}

int main() {
    pthread_t producer_thread, consumer_thread;

    // Create producer and consumer threads
    pthread_create(&producer_thread, NULL, producer, NULL);
    pthread_create(&consumer_thread, NULL, consumer, NULL);

    // Join threads to main (although in this infinite loop, it will run indefinitely)
    pthread_join(producer_thread, NULL);
    pthread_join(consumer_thread, NULL);

    return 0;
}
