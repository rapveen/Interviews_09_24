package dto;

// Value objects for inventory reporting
class InventoryCount {
    private final long totalCount;
    private final long availableCount;
    
    public InventoryCount(long totalCount, long availableCount) {
        this.totalCount = totalCount;
        this.availableCount = availableCount;
    }
    
    public long getTotalCount() {
        return totalCount;
    }
    
    public long getAvailableCount() {
        return availableCount;
    }
    
    public long getRentedCount() {
        return totalCount - availableCount;
    }
    
    @Override
    public String toString() {
        return String.format("Total: %d, Available: %d, Rented: %d", 
            totalCount, availableCount, getRentedCount());
    }
}
