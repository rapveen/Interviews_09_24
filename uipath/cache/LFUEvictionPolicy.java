package cache.eviction;

import cache.EvictionPolicy;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Least Frequently Used (LFU) eviction policy implementation.
 */
public class LFUEvictionPolicy<K> implements EvictionPolicy<K> {
    private final Map<K, Integer> keyFrequency;
    private final Map<Integer, LinkedHashSet<K>> frequencyKeys;
    private int minFrequency;

    public LFUEvictionPolicy() {
        this.keyFrequency = new HashMap<>();
        this.frequencyKeys = new HashMap<>();
        this.minFrequency = 0;
    }

    @Override
    public void keyAccessed(K key) {
        if (keyFrequency.containsKey(key)) {
            int freq = keyFrequency.get(key);
            keyFrequency.put(key, freq + 1);
            frequencyKeys.get(freq).remove(key);

            if (frequencyKeys.get(freq).isEmpty()) {
                frequencyKeys.remove(freq);
                if (freq == minFrequency) {
                    minFrequency++;
                }
            }

            frequencyKeys.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
        } else {
            keyFrequency.put(key, 1);
            frequencyKeys.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
            minFrequency = 1;
        }
    }

    @Override
    public K evictKey() {
        if (frequencyKeys.isEmpty()) {
            return null;
        }
        LinkedHashSet<K> keys = frequencyKeys.get(minFrequency);
        K evict = keys.iterator().next();
        keys.remove(evict);
        if (keys.isEmpty()) {
            frequencyKeys.remove(minFrequency);
        }
        keyFrequency.remove(evict);
        return evict;
    }

    @Override
    public void keyRemoved(K key) {
        Integer freq = keyFrequency.remove(key);
        if (freq != null) {
            LinkedHashSet<K> keys = frequencyKeys.get(freq);
            keys.remove(key);
            if (keys.isEmpty()) {
                frequencyKeys.remove(freq);
                if (freq == minFrequency) {
                    // Update minFrequency
                    if (!frequencyKeys.isEmpty()) {
                        minFrequency = frequencyKeys.keySet().stream().min(Integer::compareTo).orElse(1);
                    }
                }
            }
        }
    }
}
