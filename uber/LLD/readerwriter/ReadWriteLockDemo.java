package uber.LLD.readerwriter;


public class ReadWriteLockDemo {
    public static void main(String[] args) {
        // Run all test scenarios
        testBasicReadWrite();
        testMultipleReaders();
        testWriterPriority();
        testSimultaneousReadersWriters();
        testPotentialDeadlock();
    }

    // Test Scenario 1: Basic Read-Write Operations
    private static void testBasicReadWrite() {
        System.out.println("\nTest 1: Basic Read-Write Operations");
        SharedResource resource = new SharedResource("Initial");
        
        try {
            // Test single read
            String data = resource.read();
            assert "Initial".equals(data) : "Read data doesn't match";
            System.out.println("Single read successful");

            // Test single write
            resource.write("Updated");
            data = resource.read();
            assert "Updated".equals(data) : "Write was not successful";
            System.out.println("Single write successful");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Test Scenario 2: Multiple Readers
    private static void testMultipleReaders() {
        System.out.println("\nTest 2: Multiple Readers");
        SharedResource resource = new SharedResource("Test");
        Thread[] readers = new Thread[3];
        
        // Start multiple readers
        for (int i = 0; i < readers.length; i++) {
            readers[i] = new Thread(() -> {
                try {
                    String data = resource.read();
                    System.out.println(Thread.currentThread().getName() + " read: " + data);
                } catch (InterruptedException e) {
                    System.out.println("Reader interrupted");
                }
            }, "Reader-" + i);
            readers[i].start();
        }

        // Wait for readers to finish
        for (Thread reader : readers) {
            try {
                reader.join();
            } catch (InterruptedException e) {
                System.out.println("Join interrupted");
            }
        }

        assert resource.getLock().getReaderCount() == 0 : "Readers didn't finish";
        System.out.println("Multiple readers test successful");
    }

    private static volatile boolean readerStarted = false;
    // Test Scenario 3: Writer Priority
    private static void testWriterPriority() {
        System.out.println("\nTest 3: Writer Priority");
        SharedResource resource = new SharedResource("Initial");
        readerStarted = false;

        // Start a long-running reader
        Thread reader = new Thread(() -> {
            try {
                resource.read(); // First read
                readerStarted = true;
                resource.read(); // Second read - should be blocked by writer
            } catch (InterruptedException e) {
                System.out.println("Reader interrupted");
            }
        }, "Reader");
        reader.start();

        // Wait for reader to start
        while (!readerStarted) {
            Thread.yield();
        }

        // Start a writer
        Thread writer = new Thread(() -> {
            try {
                resource.write("Writer Update");
            } catch (InterruptedException e) {
                System.out.println("Writer interrupted");
            }
        }, "Writer");
        writer.start();

        // Try to verify writer priority
        try {
            Thread.sleep(500); // Give some time for lock interaction
            assert resource.getLock().getWaitingWritersCount() > 0 || 
                   resource.getLock().isWriting() : 
                   "Writer priority not working";
            System.out.println("Writer priority test successful");
            
            // Cleanup
            reader.join(1000);
            writer.join(1000);
        } catch (InterruptedException e) {
            System.out.println("Test interrupted");
        }
    }

    private static void testSimultaneousReadersWriters() {
        System.out.println("\nTest 1: Simultaneous Readers and Writers");
        SharedResource resource = new SharedResource("Initial");
        
        // Create multiple readers
        Thread[] readers = new Thread[3];
        for (int i = 0; i < readers.length; i++) {
            readers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 3; j++) { // Each reader tries to read 3 times
                        String data = resource.read();
                        System.out.println(Thread.currentThread().getName() + 
                                         " read: " + data);
                        Thread.sleep(100); // Small delay between reads
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " interrupted");
                }
            }, "Reader-" + i);
        }

        // Create multiple writers
        Thread[] writers = new Thread[2];
        for (int i = 0; i < writers.length; i++) {
            writers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 2; j++) { // Each writer tries to write 2 times
                        String newData = "Data-" + Thread.currentThread().getName() + "-" + j;
                        resource.write(newData);
                        System.out.println(Thread.currentThread().getName() + 
                                         " wrote: " + newData);
                        Thread.sleep(100); // Small delay between writes
                    }
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName() + " interrupted");
                }
            }, "Writer-" + i);
        }

        // Start all threads
        for (Thread reader : readers) reader.start();
        for (Thread writer : writers) writer.start();

        // Wait for completion
        try {
            for (Thread reader : readers) reader.join();
            for (Thread writer : writers) writer.join();
            System.out.println("Simultaneous readers/writers test completed");
        } catch (InterruptedException e) {
            System.out.println("Test interrupted");
        }
    }
        // Test Case 3: Potential Deadlock Scenario
    private static void testPotentialDeadlock() {
        System.out.println("\nTest 3: Potential Deadlock Scenario");
        SharedResource resource1 = new SharedResource("Resource 1");
        SharedResource resource2 = new SharedResource("Resource 2");

        // Thread 1: Tries to acquire resources in order: 1 -> 2
        Thread thread1 = new Thread(() -> {
            try {
                System.out.println("Thread 1: Attempting to read resource 1");
                resource1.read();
                Thread.sleep(100); // Delay to increase chance of deadlock
                System.out.println("Thread 1: Attempting to read resource 2");
                resource2.read();
            } catch (InterruptedException e) {
                System.out.println("Thread 1 interrupted");
            }
        }, "Thread-1");

        // Thread 2: Tries to acquire resources in order: 2 -> 1
        Thread thread2 = new Thread(() -> {
            try {
                System.out.println("Thread 2: Attempting to read resource 2");
                resource2.read();
                Thread.sleep(100); // Delay to increase chance of deadlock
                System.out.println("Thread 2: Attempting to read resource 1");
                resource1.read();
            } catch (InterruptedException e) {
                System.out.println("Thread 2 interrupted");
            }
        }, "Thread-2");

        thread1.start();
        thread2.start();

        // Wait for a while to see if threads complete
        try {
            Thread.sleep(2000);
            System.out.println("Checking if threads are still alive (potential deadlock):");
            System.out.println("Thread 1 alive: " + thread1.isAlive());
            System.out.println("Thread 2 alive: " + thread2.isAlive());
            
            // For interview purposes, we'll interrupt the threads if they're deadlocked
            if (thread1.isAlive() || thread2.isAlive()) {
                System.out.println("Potential deadlock detected!");
                thread1.interrupt();
                thread2.interrupt();
            }
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
        }
    }

}
