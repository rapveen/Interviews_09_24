package uber.LLD;

// Package structure

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// --- Interfaces ---

/**
 * Core storage engine interface
 */
interface StorageEngine {
    void write(String key, byte[] value, Transaction txn);
    byte[] read(String key, Transaction txn);
    void delete(String key, Transaction txn);
    Snapshot getSnapshot();
}

/**
 * Interface for different storage implementations
 */
interface Storage {
    void put(String key, byte[] value);
    byte[] get(String key);
    void remove(String key);
    boolean containsKey(String key);
}

// --- Value Objects and Data Classes ---

/**
 * Represents a point-in-time view of the data
 */
class Snapshot {
    private final Map<String, byte[]> data;
    private final long version;

    public Snapshot(Map<String, byte[]> data, long version) {
        this.data = new HashMap<>(data);
        this.version = version;
    }

    public byte[] get(String key) {
        return data.get(key);
    }

    public long getVersion() {
        return version;
    }
}

/**
 * Represents a single operation in a transaction
 */
class Operation {
    enum Type { PUT, DELETE }
    
    private final Type type;
    private final String key;
    private final byte[] value;
    private final byte[] oldValue;

    public Operation(Type type, String key, byte[] value, byte[] oldValue) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.oldValue = oldValue;
    }

    public Type getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] getOldValue() {
        return oldValue;
    }
}

// --- Core Classes ---

/**
 * Main transaction class
 */
class Transaction {
    private final UUID txnId;
    private final Map<String, Operation> operations;
    private boolean isActive;
    private final TransactionManager manager;

    public Transaction(TransactionManager manager) {
        this.txnId = UUID.randomUUID();
        this.operations = new HashMap<>();
        this.isActive = true;
        this.manager = manager;
    }

    public void addOperation(Operation op) {
        if (!isActive) {
            throw new IllegalStateException("Transaction is not active");
        }
        operations.put(op.getKey(), op);
    }

    public void commit() {
        if (!isActive) {
            throw new IllegalStateException("Transaction is not active");
        }
        manager.commitTransaction(this);
        isActive = false;
    }

    public void rollback() {
        if (!isActive) {
            throw new IllegalStateException("Transaction is not active");
        }
        manager.rollbackTransaction(this);
        isActive = false;
    }

    public Map<String, Operation> getOperations() {
        return Collections.unmodifiableMap(operations);
    }

    public UUID getTxnId() {
        return txnId;
    }

    public boolean isActive() {
        return isActive;
    }
}

/**
 * Manages transaction lifecycle and coordination
 */
class TransactionManager {
    private final Map<UUID, Transaction> activeTransactions;
    private final StorageEngine storageEngine;

    public TransactionManager(StorageEngine storageEngine) {
        this.activeTransactions = new ConcurrentHashMap<>();
        this.storageEngine = storageEngine;
    }

    public Transaction beginTransaction() {
        Transaction txn = new Transaction(this);
        activeTransactions.put(txn.getTxnId(), txn);
        return txn;
    }

    public void commitTransaction(Transaction txn) {
        if (!activeTransactions.containsKey(txn.getTxnId())) {
            throw new IllegalStateException("Transaction not found");
        }

        // Apply all operations
        for (Operation op : txn.getOperations().values()) {
            switch (op.getType()) {
                case PUT:
                    storageEngine.write(op.getKey(), op.getValue(), txn);
                    break;
                case DELETE:
                    storageEngine.delete(op.getKey(), txn);
                    break;
            }
        }

        activeTransactions.remove(txn.getTxnId());
    }

    public void rollbackTransaction(Transaction txn) {
        // Simply remove the transaction without applying changes
        activeTransactions.remove(txn.getTxnId());
    }
}

/**
 * LSM-tree based storage engine implementation
 */
class LSMStorageEngine implements StorageEngine {
    private final MemTable memTable;
    private final List<SSTable> ssTables;
    private final AtomicLong version;

    public LSMStorageEngine() {
        this.memTable = new MemTable();
        this.ssTables = new ArrayList<>();
        this.version = new AtomicLong(0);
    }

    @Override
    public void write(String key, byte[] value, Transaction txn) {
        memTable.put(key, value);
        version.incrementAndGet();
        
        // Check if memTable needs flushing
        if (memTable.shouldFlush()) {
            flush();
        }
    }

    @Override
    public byte[] read(String key, Transaction txn) {
        // First check memTable
        byte[] value = memTable.get(key);
        if (value != null) {
            return value;
        }

        // Then check SSTables from newest to oldest
        for (SSTable ssTable : ssTables) {
            value = ssTable.get(key);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    @Override
    public void delete(String key, Transaction txn) {
        memTable.remove(key);
        version.incrementAndGet();
    }

    @Override
    public Snapshot getSnapshot() {
        Map<String, byte[]> data = new HashMap<>();
        
        // Add all data from memTable
        memTable.getAllEntries().forEach(data::put);
        
        // Add all data from SSTables (newer entries override older ones)
        for (SSTable ssTable : ssTables) {
            ssTable.getAllEntries().forEach(data::put);
        }
        
        return new Snapshot(data, version.get());
    }

    private void flush() {
        // Create new SSTable from memTable
        SSTable ssTable = new SSTable(memTable.getAllEntries());
        ssTables.add(0, ssTable); // Add to front as it's the newest
        
        // Clear memTable
        memTable.clear();
        
        // Trigger compaction if needed
        if (shouldCompact()) {
            compact();
        }
    }

    private boolean shouldCompact() {
        return ssTables.size() > 3; // Simple compaction strategy
    }

    private void compact() {
        // Simple compaction: merge last two SSTables
        if (ssTables.size() < 2) return;
        
        SSTable table1 = ssTables.remove(ssTables.size() - 1);
        SSTable table2 = ssTables.remove(ssTables.size() - 1);
        
        Map<String, byte[]> mergedData = new HashMap<>(table1.getAllEntries());
        mergedData.putAll(table2.getAllEntries());
        
        ssTables.add(new SSTable(mergedData));
    }
}

/**
 * In-memory storage implementation
 */
class MemTable implements Storage {
    private final Map<String, byte[]> data;
    private static final int FLUSH_THRESHOLD = 1000; // Number of entries before flush

    public MemTable() {
        this.data = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String key, byte[] value) {
        data.put(key, value);
    }

    @Override
    public byte[] get(String key) {
        return data.get(key);
    }

    @Override
    public void remove(String key) {
        data.remove(key);
    }

    @Override
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public Map<String, byte[]> getAllEntries() {
        return new HashMap<>(data);
    }

    public boolean shouldFlush() {
        return data.size() >= FLUSH_THRESHOLD;
    }

    public void clear() {
        data.clear();
    }
}

/**
 * Disk-based storage implementation
 */
class SSTable implements Storage {
    private final Map<String, byte[]> data;
    private final BloomFilter bloomFilter;

    public SSTable(Map<String, byte[]> data) {
        this.data = new HashMap<>(data);
        this.bloomFilter = new BloomFilter(data.size());
        data.keySet().forEach(bloomFilter::add);
    }

    @Override
    public void put(String key, byte[] value) {
        throw new UnsupportedOperationException("SSTable is immutable");
    }

    @Override
    public byte[] get(String key) {
        if (!bloomFilter.mightContain(key)) {
            return null;
        }
        return data.get(key);
    }

    @Override
    public void remove(String key) {
        throw new UnsupportedOperationException("SSTable is immutable");
    }

    @Override
    public boolean containsKey(String key) {
        return data.containsKey(key);
    }

    public Map<String, byte[]> getAllEntries() {
        return new HashMap<>(data);
    }
}

/**
 * Simple Bloom filter implementation
 */
class BloomFilter {
    private final BitSet bitSet;
    private final int size;
    private final int hashFunctions;

    public BloomFilter(int expectedElements) {
        this.size = expectedElements * 10; // 10 bits per element
        this.hashFunctions = 3; // Number of hash functions
        this.bitSet = new BitSet(size);
    }

    public void add(String key) {
        for (int i = 0; i < hashFunctions; i++) {
            bitSet.set(getHash(key, i));
        }
    }

    public boolean mightContain(String key) {
        for (int i = 0; i < hashFunctions; i++) {
            if (!bitSet.get(getHash(key, i))) {
                return false;
            }
        }
        return true;
    }

    private int getHash(String key, int seed) {
        return Math.abs((key.hashCode() + seed * 31) % size);
    }
}

/**
 * Main client-facing class
 */
public class KeyValueStore {
    private final StorageEngine storageEngine;
    private final TransactionManager txnManager;

    public KeyValueStore() {
        this.storageEngine = new LSMStorageEngine();
        this.txnManager = new TransactionManager(storageEngine);
    }

    public void put(String key, byte[] value) {
        Transaction txn = txnManager.beginTransaction();
        try {
            byte[] oldValue = storageEngine.read(key, txn);
            Operation op = new Operation(Operation.Type.PUT, key, value, oldValue);
            txn.addOperation(op);
            txn.commit();
        } catch (Exception e) {
            txn.rollback();
            throw e;
        }
    }

    public byte[] get(String key) {
        Transaction txn = txnManager.beginTransaction();
        try {
            byte[] value = storageEngine.read(key, txn);
            txn.commit();
            return value;
        } catch (Exception e) {
            txn.rollback();
            throw e;
        }
    }

    public void delete(String key) {
        Transaction txn = txnManager.beginTransaction();
        try {
            byte[] oldValue = storageEngine.read(key, txn);
            Operation op = new Operation(Operation.Type.DELETE, key, null, oldValue);
            txn.addOperation(op);
            txn.commit();
        } catch (Exception e) {
            txn.rollback();
            throw e;
        }
    }

    public Transaction beginTransaction() {
        return txnManager.beginTransaction();
    }

    public Snapshot getSnapshot() {
        return storageEngine.getSnapshot();
    }

    // Example usage
    public static void main(String[] args) {
        KeyValueStore store = new KeyValueStore();

        // Simple put/get operations
        store.put("key1", "Hello".getBytes());
        store.put("key2", "World".getBytes());

        System.out.println("Key1: " + new String(store.get("key1")));
        System.out.println("Key2: " + new String(store.get("key2")));

        // Transaction example
        Transaction txn = store.beginTransaction();
        try {
            byte[] oldValue = store.get("key1");
            Operation op = new Operation(Operation.Type.PUT, "key1", 
                "Updated Hello".getBytes(), oldValue);
            txn.addOperation(op);
            
            op = new Operation(Operation.Type.PUT, "key3", 
                "Transaction".getBytes(), null);
            txn.addOperation(op);
            
            txn.commit();
        } catch (Exception e) {
            txn.rollback();
            e.printStackTrace();
        }

        System.out.println("After transaction:");
        System.out.println("Key1: " + new String(store.get("key1")));
        System.out.println("Key3: " + new String(store.get("key3")));

        // Get snapshot
        Snapshot snapshot = store.getSnapshot();
        System.out.println("Snapshot version: " + snapshot.getVersion());
    }
}
