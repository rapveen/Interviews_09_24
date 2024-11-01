package controller;

package com.rental.controller;

import com.rental.dto.RentalReceipt;
import com.rental.dto.RentalInfo;
import com.rental.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<RentalReceipt> rentProduct(@RequestBody RentProductRequest request) {
        RentalReceipt receipt = rentalService.rentProduct(request.getProductId(), request.getCustomerId(), request.getDays());
        return new ResponseEntity<>(receipt, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public ResponseEntity<List<RentalInfo>> getActiveRentals() {
        List<RentalInfo> rentals = rentalService.getActiveRentals();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<RentalInfo>> getOverdueRentals() {
        List<RentalInfo> overdueRentals = rentalService.getOverdueRentals();
        return ResponseEntity.ok(overdueRentals);
    }

    // Additional endpoints as needed
}