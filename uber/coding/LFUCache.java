/*
 * Why Two Maps?
 
 nodeMap (Key → Node):
 
 Provides O(1) access to any key's node
 Essential for quick get/put operations
 
 
 countMap (Frequency → DLList):
 
 Groups nodes by frequency
 Maintains LRU order within each frequency using DLList
 Enables O(1) eviction of least frequent item
 
 
 
 
 Why Doubly Linked Lists?
 
 O(1) removal from middle (when updating frequency)
 O(1) addition at head (most recent)
 O(1) removal from tail (least recent)
 Perfect for maintaining order within same frequency
 
 
 Why Track leastFrequencyCount?
 
 Quick identification of which frequency list to evict from
 No need to scan through frequencies
 Updated when:
 
 Removing last item from current least frequency
 Adding new item (always starts at frequency 1)

 Dry run:

 Initial: empty cache with capacity 3
 1. put(1,1): freq 1 → [1]
 2. put(2,2): freq 1 → [2,1]
 3. put(3,3): freq 1 → [3,2,1]
 4. get(1): 
    - freq 1 → [3,2]
    - freq 2 → [1]
 5. put(4,4) when full:
    - Evict from freq 1 (lowest) → removes 2
    - freq 1 → [4,3]
    - freq 2 → [1]
    
 * 
 */

 import java.util.HashMap;
 import java.util.Map;
class LFUCache {

    public static class Node {
        private final int key;
        private int val;
        private int freq;
        private Node prev, next;
        Node(int key, int val) {
            this.key = key;
            this.val = val;
            this.freq = 1;
        }
    }

    public static class DLList {
        private final Node head;
        private final Node tail;
        private int size;
        DLList() {
            head = new Node(0,0);
            tail  = new Node(0,0);
            head.next = tail;
            tail.prev = head;
            this.size = 0;
        }

        void addFirst(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            this.size++;
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
            this.size--;
        }

        Node removeLast() {
            if (this.size > 0) {
                Node node = tail.prev;
                remove(node);
                return node;
            }
            else return null;
        }

        boolean isEmpty() {
            return this.size==0;
        }
    }

    private int capacity;
    Map<Integer, Node> cache;
    Map<Integer, DLList> freqMap;
    private int minFreq;

    public LFUCache(int capacity) {
        if(capacity < 0) {
            throw new IllegalArgumentException();
        }
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.minFreq = 0;
    }
    
    public int get(int key) {
        Node node = cache.get(key);
        if(node != null) {
            updateFreq(node);
            return node.val;
        }
        return -1;
    }
    
    public void put(int key, int value) {
        if (this.capacity == 0) {
            return;
        }
        Node node = cache.get(key);
        if(node != null) {
            node.val = value;
            updateFreq(node);
        } else {
            if(cache.size() >= this.capacity) {
                DLList minFreqList  = freqMap.get(this.minFreq);
                Node lruNode = minFreqList.removeLast();
                cache.remove(lruNode.key);
                 // Clean up empty frequency list
                if (minFreqList.isEmpty()) {
                    freqMap.remove(minFreq);
                }
            }
            this.minFreq = 1;
            node = new Node(key, value);
            cache.put(key, node);
            freqMap.computeIfAbsent(1, k -> new DLList()).addFirst(node);
        }
    }

    private void updateFreq(Node node) {
        DLList currentNodeList = freqMap.get(node.freq);
        //remove node from the currlist
        currentNodeList.remove(node);

         // 3. Critical Section: Handle empty frequency list
        if (node.freq == minFreq && currentNodeList.isEmpty()) {
            // Increment minFreq since this frequency no longer exists
            minFreq++;
            // If we're removing from minimum frequency AND
            // this was the last node at this frequency:
            // a) Remove the empty list from freqMap
            freqMap.remove(node.freq);
        }

        // update the freq of node to +1
        node.freq++;

        // add to new freq list
        freqMap.computeIfAbsent(node.freq, k->new DLList()).addFirst(node);
    }

    public static void main(String[] args) {
        System.out.println("=== LFU Cache Demonstration ===\n");

        // Test Case 1: Basic Operations from the dry run example
        System.out.println("Test Case 1: Basic Operations (Capacity 3)");
        LFUCache cache = new LFUCache(3);
        
        System.out.println("\nStep 1: put(1,1)");
        cache.put(1, 1);
        printCacheState(cache);
        
        System.out.println("\nStep 2: put(2,2)");
        cache.put(2, 2);
        printCacheState(cache);
        
        System.out.println("\nStep 3: put(3,3)");
        cache.put(3, 3);
        printCacheState(cache);
        
        System.out.println("\nStep 4: get(1)");
        int value = cache.get(1);
        System.out.println("Retrieved value: " + value);
        printCacheState(cache);
        
        System.out.println("\nStep 5: put(4,4)");
        cache.put(4, 4);  // Should evict 2 (least frequent)
        printCacheState(cache);

        // Test Case 2: Advanced Operations
        System.out.println("\n=== Test Case 2: Advanced Operations ===");
        cache = new LFUCache(3);
        
        System.out.println("\nAdding initial values: 1,2,3");
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(3, 3);
        printCacheState(cache);
        
        System.out.println("\nAccessing 1 twice");
        cache.get(1);
        cache.get(1);
        printCacheState(cache);
        
        System.out.println("\nAccessing 2 once");
        cache.get(2);
        printCacheState(cache);
        
        System.out.println("\nAdding new value 4");
        cache.put(4, 4);  // Should evict 3 (least frequent)
        printCacheState(cache);
        
        // Test Case 3: Update Existing Value
        System.out.println("\n=== Test Case 3: Updating Existing Value ===");
        System.out.println("\nUpdating value of key 1 from 1 to 10");
        cache.put(1, 10);
        printCacheState(cache);
    }
    
    private static void printCacheState(LFUCache cache) {
        System.out.println("Cache state:");
        // Note: This is for demonstration. In real implementation,
        // we would need to add methods to expose internal state.
        System.out.println("Items in cache: " + cache.cache.size());
        System.out.println("Frequency groups: " + cache.freqMap.size());
        System.out.println("Minimum frequency: " + cache.minFreq);
        
        System.out.println("\nDetailed frequency map:");
        for (Map.Entry<Integer, LFUCache.DLList> entry : cache.freqMap.entrySet()) {
            System.out.print("Frequency " + entry.getKey() + ": ");
            printList(entry.getValue());
        }
        System.out.println();
    }
    
    private static void printList(LFUCache.DLList list) {
        LFUCache.Node current = list.head.next;
        System.out.print("[");
        while (current != list.tail) {
            System.out.print("(" + current.key + "," + current.val + ")");
            current = current.next;
            if (current != list.tail) {
                System.out.print(" -> ");
            }
        }
        System.out.println("]");
    }
}