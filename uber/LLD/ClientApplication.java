package uber.LLD;

import java.util.*;

interface TransactionalKeyValueStore {
    long beginTransaction();
    void commitTransaction(long transactionId);
    void rollbackTransaction(long transactionId);
    void put(String key, String value, long transactionId);
    String get(String key);
    void delete(String key, long transactionId);
}

class KeyValueStore {
    private final String key;
    private String value;
    private final Date createdAt;
    private Date updatedAt;

    public KeyValueStore(String key, String value) {
        this.key = key;
        this.value = value;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.updatedAt = new Date();
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}

class TransactionLog {
    private final long transactionId;
    private String status; // active, committed, or rolled back
    private final Date startTime;
    private Date endTime;
    private final List<TransactionHistory> history;

    public TransactionLog(long transactionId) {
        this.transactionId = transactionId;
        this.status = "active";
        this.startTime = new Date();
        this.history = new ArrayList<>();
    }

    public long getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void commit() {
        this.status = "committed";
        this.endTime = new Date();
    }

    public void rollback() {
        this.status = "rolled back";
        this.endTime = new Date();
    }

    public void addHistory(TransactionHistory historyEntry) {
        history.add(historyEntry);
    }

    public List<TransactionHistory> getHistory() {
        return history;
    }
}

class TransactionHistory {
    private final String key;
    private final String oldValue;
    private final String newValue;
    private final Date timestamp;

    public TransactionHistory(String key, String oldValue, String newValue) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = new Date();
    }

    public String getKey() {
        return key;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}

class KeyValueStorageService implements TransactionalKeyValueStore {
    private final Map<String, KeyValueStore> store;
    private final Map<Long, TransactionLog> transactions;
    private long transactionCounter = 0;

    public KeyValueStorageService() {
        this.store = new HashMap<>();
        this.transactions = new HashMap<>();
    }

    @Override
    public long beginTransaction() {
        long transactionId = ++transactionCounter;
        transactions.put(transactionId, new TransactionLog(transactionId));
        System.out.println("Transaction " + transactionId + " started.");
        return transactionId;
    }

    @Override
    public void commitTransaction(long transactionId) {
        TransactionLog transaction = transactions.get(transactionId);
        if (transaction != null && "active".equals(transaction.getStatus())) {
            transaction.commit();
            System.out.println("Transaction " + transactionId + " committed.");
        } else {
            System.out.println("Transaction " + transactionId + " not found or already ended.");
        }
    }

    @Override
    public void rollbackTransaction(long transactionId) {
        TransactionLog transaction = transactions.get(transactionId);
        if (transaction != null && "active".equals(transaction.getStatus())) {
            for (TransactionHistory history : transaction.getHistory()) {
                KeyValueStore kv = store.get(history.getKey());
                if (kv != null) {
                    kv.setValue(history.getOldValue());
                }
            }
            transaction.rollback();
            System.out.println("Transaction " + transactionId + " rolled back.");
        } else {
            System.out.println("Transaction " + transactionId + " not found or already ended.");
        }
    }

    @Override
    public void put(String key, String value, long transactionId) {
        TransactionLog transaction = transactions.get(transactionId);
        if (transaction == null || !"active".equals(transaction.getStatus())) {
            System.out.println("Invalid transaction: " + transactionId);
            return;
        }

        KeyValueStore kv = store.get(key);
        String oldValue = kv != null ? kv.getValue() : null;
        if (kv == null) {
            kv = new KeyValueStore(key, value);
            store.put(key, kv);
        } else {
            kv.setValue(value);
        }
        transaction.addHistory(new TransactionHistory(key, oldValue, value));
    }

    @Override
    public String get(String key) {
        KeyValueStore kv = store.get(key);
        return kv != null ? kv.getValue() : null;
    }

    @Override
    public void delete(String key, long transactionId) {
        TransactionLog transaction = transactions.get(transactionId);
        if (transaction == null || !"active".equals(transaction.getStatus())) {
            System.out.println("Invalid transaction: " + transactionId);
            return;
        }

        KeyValueStore kv = store.get(key);
        if (kv != null) {
            transaction.addHistory(new TransactionHistory(key, kv.getValue(), null));
            store.remove(key);
        }
    }
}

public class ClientApplication {
    public static void main(String[] args) {
        KeyValueStorageService storage = new KeyValueStorageService();

        long txn1 = storage.beginTransaction();
        storage.put("user1", "John", txn1);
        storage.put("user2", "Jane", txn1);
        System.out.println("user1: " + storage.get("user1"));
        storage.commitTransaction(txn1);

        long txn2 = storage.beginTransaction();
        storage.put("user1", "Alice", txn2);
        System.out.println("user1 (within txn2): " + storage.get("user1"));
        storage.rollbackTransaction(txn2);
        System.out.println("user1 (after rollback): " + storage.get("user1"));

        long txn3 = storage.beginTransaction();
        storage.delete("user1", txn3);
        System.out.println("user1 (after delete in txn3): " + storage.get("user1"));
        storage.commitTransaction(txn3);
        System.out.println("user1 (after commit txn3): " + storage.get("user1"));
    }
}
