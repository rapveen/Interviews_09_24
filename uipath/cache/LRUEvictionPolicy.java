package uipath.cache;

import uipath.cache.EvictionPolicy;

import java.util.HashMap;
import java.util.Map;

/**
 * Least Recently Used (LRU) eviction policy implementation using a custom Doubly Linked List.
 */
public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {
    private final Map<K, Node<K>> map;
    private final DoublyLinkedList<K> dll;

    public LRUEvictionPolicy() {
        this.map = new HashMap<>();
        this.dll = new DoublyLinkedList<>();
    }

    @Override
    public void keyAccessed(K key) {
        if (map.containsKey(key)) {
            Node<K> node = map.get(key);
            dll.moveToEnd(node);
        } else {
            Node<K> newNode = new Node<>(key);
            dll.addLast(newNode);
            map.put(key, newNode);
        }
    }

    @Override
    public K evictKey() {
        Node<K> node = dll.removeFirst();
        if (node != null) {
            map.remove(node.key);
            return node.key;
        }
        return null;
    }

    @Override
    public void keyRemoved(K key) {
        Node<K> node = map.remove(key);
        if (node != null) {
            dll.remove(node);
        }
    }

    // Inner classes for Node and DoublyLinkedList
    private static class Node<K> {
        K key;
        Node<K> prev;
        Node<K> next;

        Node(K key) {
            this.key = key;
        }
    }

    private static class DoublyLinkedList<K> {
        private final Node<K> head;
        private final Node<K> tail;

        DoublyLinkedList() {
            head = new Node<>(null);
            tail = new Node<>(null);
            head.next = tail;
            tail.prev = head;
        }

        void addLast(Node<K> node) {
            node.prev = tail.prev;
            node.next = tail;
            tail.prev.next = node;
            tail.prev = node;
        }

        void moveToEnd(Node<K> node) {
            remove(node);
            addLast(node);
        }

        Node<K> removeFirst() {
            if (head.next == tail) {
                return null;
            }
            Node<K> first = head.next;
            remove(first);
            return first;
        }

        void remove(Node<K> node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
        }
    }
}

