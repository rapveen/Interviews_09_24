package cache.factory;

import cache.EvictionPolicy;
import cache.eviction.LFUEvictionPolicy;
import cache.eviction.LIFOVictionPolicy;
import cache.eviction.LRUEvictionPolicy;

/**
 * Factory to create EvictionPolicy instances.
 */
public class EvictionPolicyFactory {
    public enum PolicyType {
        LRU,
        LFU,
        LIFO
    }

    public static <K> EvictionPolicy<K> createEvictionPolicy(PolicyType type) {
        switch (type) {
            case LRU:
                return new LRUEvictionPolicy<>();
            case LFU:
                return new LFUEvictionPolicy<>();
            case LIFO:
                return new LIFOVictionPolicy<>();
            default:
                throw new IllegalArgumentException("Unknown Eviction Policy Type");
        }
    }
}
