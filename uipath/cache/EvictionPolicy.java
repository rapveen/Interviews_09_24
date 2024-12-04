package uipath.cache;

/**
 * Defines the contract for eviction policies.
 */
public interface EvictionPolicy<K> {
    /**
     * To be called when a key is accessed (get or put).
     *
     * @param key The key that was accessed.
     */
    void keyAccessed(K key);

    /**
     * Determines which key to evict based on the policy.
     *
     * @return The key to evict.
     */
    K evictKey();

    /**
     * To be called when a key is removed from the cache.
     *
     * @param key The key that was removed.
     */
    void keyRemoved(K key);
}

