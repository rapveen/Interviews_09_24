package cache.eviction;

import cache.EvictionPolicy;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Last In First Out (LIFO) eviction policy implementation.
 */
public class LIFOVictionPolicy<K> implements EvictionPolicy<K> {
    private final Deque<K> stack;

    public LIFOVictionPolicy() {
        this.stack = new ArrayDeque<>();
    }

    @Override
    public void keyAccessed(K key) {
        stack.remove(key); // O(n), acceptable for LIFO in interview setting
        stack.push(key);
    }

    @Override
    public K evictKey() {
        return stack.poll();
    }

    @Override
    public void keyRemoved(K key) {
        stack.remove(key);
    }
}
