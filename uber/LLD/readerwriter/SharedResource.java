package uber.LLD.readerwriter;

public class SharedResource {
    private String data;
    private final ReadWriteLock lock;

    public SharedResource(String initialData) {
        this.data = initialData;
        this.lock = new ReadWriteLock();
    }

    public String read() throws InterruptedException {
        lock.acquireReadLock();
        try {
            // Simulate some reading time
            Thread.sleep(100);
            return this.data;
        } finally {
            lock.releaseReadLock();
        }
    }

    public void write(String newData) throws InterruptedException {
        lock.acquireWriteLock();
        try {
            // Simulate some writing time
            Thread.sleep(200);
            this.data = newData;
        } finally {
            lock.releaseWriteLock();
        }
    }

    // For testing purposes
    ReadWriteLock getLock() {
        return lock;
    }
}


