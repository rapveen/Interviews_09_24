package uber.LLD.readerwriter;

import java.util.*;

public class ReadWriteLock {
    private int readers = 0;
    private boolean isWriting = false;
    private final Queue<Thread> waitingWriters = new LinkedList<>();
    private static final long DEFAULT_TIMEOUT = 1000; // 1 second timeout

    public synchronized void acquireReadLock() throws InterruptedException {
        while (isWriting || !waitingWriters.isEmpty()) {
            wait();
        }
        readers++;
    }

    public synchronized void releaseReadLock() {
        readers--;
        if (readers == 0) {
            notifyAll(); // Wake up waiting writers
        }
    }

    public synchronized void acquireWriteLock() throws InterruptedException {
        Thread current = Thread.currentThread();
        waitingWriters.offer(current);
        
        while ((readers > 0 || isWriting) || waitingWriters.peek() != current) {
            wait();
        }
        waitingWriters.poll();
        isWriting = true;
    }

    public synchronized void releaseWriteLock() {
        isWriting = false;
        notifyAll();
    }

    // For testing purposes
    synchronized int getReaderCount() {
        return readers;
    }

    synchronized boolean isWriting() {
        return isWriting;
    }

    synchronized int getWaitingWritersCount() {
        return waitingWriters.size();
    }
}


