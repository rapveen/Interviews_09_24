/*
 * Why Two Maps?
 
 nodeMap (Key → Node):
 
 Provides O(1) access to any key's node
 Essential for quick get/put operations
 
 
 countMap (Frequency → DLList):
 
 Groups nodes by frequency
 Maintains LRU order within each frequency
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
            head.next.prev = node;
   

import java.util.HashMap;
import java.util.Map;

        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            this.size--;
        }

        Node removeLast() {
            if (size > 0) {
                Node node = tail.prev;
                remove(node);
                return node;
            }
            else return null;
        }

        boolean isEmpty() {
            return size==0;
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
                DLList list = freqMap.get(this.minFreq);
                Node lruNode = list.removeLast();
                cache.remove(lruNode.key);
                 // Clean up empty frequency list
                if (list.isEmpty()) {
                    freqMap.remove(minFreq);
                }
            }
            this.minFreq = 1;
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            freqMap.computeIfAbsent(1, k -> new DLList()).addFirst(newNode);
        }
    }

    private void updateFreq(Node node) {
        DLList currentNodeList = freqMap.get(node.freq);
        //remove node from the currlist
        currentNodeList.remove(node);

         // Update minFrequency if necessary
        if (node.freq == minFreq && currentNodeList.isEmpty()) {
            minFreq++;
        }

        // update the freq of node to +1
        node.freq++;

        // add to next freq list
        freqMap.computeIfAbsent(node.freq, k->new DLList()).addFirst(node);
    }
}

/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache obj = new LFUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */