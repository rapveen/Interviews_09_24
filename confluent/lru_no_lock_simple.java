/** 
Key Questions for Clarification:
Cache Specifications:
Cache size limit?
Types of values stored (objects, primitives)?
Memory constraints?
Concurrency Requirements:
Expected read:write ratio?
Maximum concurrent operations?
Is strict consistency required or eventual consistency acceptable?
Performance Expectations:
Acceptable latency for reads/writes?
Priority between read vs write performance?
Lock Granularity:
Segment-level vs key-level locking?
Read-write lock vs mutex preference?
Edge Cases:
Cache invalidation strategy when full?
Handling failed writes/network partitions?
Dealing with deadlocks?
Recovery mechanism after crashes?
Additional Features:
Need for expiration/TTL?
Statistics/metrics requirements?
Cache warm-up strategy?
Sequential Solution Evolution:
Brute Force:
Single global lock for all operations
HashMap + Doubly linked list for LRU Pros: Simple, guarantees consistency Cons: High 
contention, poor scalability, bottleneck for concurrent ops
 **/
import java.util.Hashtable;
 
public class LRUCache {
class DLinkedNode {
  int key;
  int value;
  DLinkedNode pre;
  DLinkedNode post;
}
/**
 * Always add the new node right after head;
 */
private void addNode(DLinkedNode node) {
   
  node.pre = head;
  node.post = head.post;
  head.post.pre = node;
  head.post = node;
}
/**
 * Remove an existing node from the linked list.
 */
private void removeNode(DLinkedNode node){
  DLinkedNode pre = node.pre;
  DLinkedNode post = node.post;
  pre.post = post;
  post.pre = pre;
}
/**
 * Move certain node in between to the head.
 */
private void moveToHead(DLinkedNode node){
  this.removeNode(node);
  this.addNode(node);
}
// pop the current tail.
private DLinkedNode popTail(){
  DLinkedNode res = tail.pre;
  this.removeNode(res);
  return res;
}
private Hashtable<Integer, DLinkedNode>
  cache = new Hashtable<Integer, DLinkedNode>();
private int count;
private int capacity;
private DLinkedNode head, tail;
public LRUCache(int capacity) {
  this.count = 0;
  this.capacity = capacity;
  head = new DLinkedNode();
  head.pre = null;
  tail = new DLinkedNode();
  tail.post = null;
  head.post = tail;
  tail.pre = head;
}
public int get(int key) {
  DLinkedNode node = cache.get(key);
  if(node == null){
    return -1; // should raise exception here.
  }
  // move the accessed node to the head;
  this.moveToHead(node);
  return node.value;
}
 
public void put(int key, int value) {
  DLinkedNode node = cache.get(key);
  if(node == null){
    DLinkedNode newNode = new DLinkedNode();
    newNode.key = key;
    newNode.value = value;
    this.cache.put(key, newNode);
    this.addNode(newNode);
    ++count;
    if(count > capacity){
      // pop the tail
      DLinkedNode tail = this.popTail();
      this.cache.remove(tail.key);
      --count;
    }
  }else{
    // update the value.
    node.value = value;
    this.moveToHead(node);
  }
}
}
