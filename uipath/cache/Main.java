package main;

import cache.Cache;
import cache.EvictionPolicy;
import cache.factory.EvictionPolicyFactory;
import cache.factory.EvictionPolicyFactory.PolicyType;

/**
 * Demonstrates the Cache Service functionality.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize Cache with LRU Eviction Policy
        EvictionPolicyFactory.PolicyType policyType = PolicyType.LRU;
        EvictionPolicy<Integer> evictionPolicy = EvictionPolicyFactory.createEvictionPolicy(policyType);
        Cache<Integer, String> cache = new Cache<>(3, evictionPolicy);

        // Perform Cache Operations
        System.out.println("Putting key=1, value='A'");
        cache.put(1, "A");

        System.out.println("Putting key=2, value='B'");
        cache.put(2, "B");

        System.out.println("Putting key=3, value='C'");
        cache.put(3, "C");

        System.out.println("Getting key=1: " + cache.get(1)); // Access key=1

        System.out.println("Putting key=4, value='D' (should evict key=2)");
        cache.put(4, "D");

        System.out.println("Getting key=2: " + cache.get(2)); // Attempt to access evicted key=2

        System.out.println("Getting key=3: " + cache.get(3));

        System.out.println("Getting key=4: " + cache.get(4));

        System.out.println("Deleting key=1");
        cache.delete(1);

        System.out.println("Clearing cache");
        cache.clear();

        // Attempt to get a key after clearing
        System.out.println("Getting key=3 after clearing: " + cache.get(3));
    }
}
