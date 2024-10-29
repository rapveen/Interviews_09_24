package dto;

public class BikeInventoryReport {
    private final Map<Size, Long> totalCounts;
    private final Map<Size, Long> availableCounts;
    
    public BikeInventoryReport(Map<Size, Long> totalCounts, Map<Size, Long> availableCounts) {
        this.totalCounts = totalCounts;
        this.availableCounts = availableCounts;
    }
    
    public InventoryCount getCountForSize(Size size) {
        long total = totalCounts.getOrDefault(size, 0L);
        long available = availableCounts.getOrDefault(size, 0L);
        return new InventoryCount(total, available);
    }
    
    public Map<Size, InventoryCount> getAllCounts() {
        return Arrays.stream(Size.values())
            .collect(Collectors.toMap(
                size -> size,
                this::getCountForSize
            ));
    }
    
    @Override
    public String toString() {
        StringBuilder report = new StringBuilder("Bike Inventory Report:\n");
        getAllCounts().forEach((size, count) -> 
            report.append(String.format("%s bikes: %s\n", size, count))
        );
        return report.toString();
    }
}

