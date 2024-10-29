package controller;

@RestController
@RequestMapping("/api/inventory")
class InventoryController {
    private final RentalService rentalService;
    
    @GetMapping("/bikes/small/count")
    public InventoryCount getSmallBikesCount() {
        return rentalService.getSmallBikesCount();
    }
    
    @GetMapping("/bikes/{size}/count")
    public InventoryCount getBikesCountBySize(@PathVariable Size size) {
        return rentalService.getBikesCountBySize(size);
    }
    
    @GetMapping("/bikes/report")
    public BikeInventoryReport getFullBikeInventoryReport() {
        return rentalService.getFullBikeInventoryReport();
    }
}

