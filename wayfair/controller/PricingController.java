package controller;

package com.rental.controller;

import com.rental.dto.PricingResponse;
import com.rental.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing")
public class PricingController {
    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<PricingResponse> calculateRentalPrice(
            @RequestParam String productId,
            @RequestParam int days) {
        PricingResponse response = pricingService.calculatePrice(productId, days);
        return ResponseEntity.ok(response);
    }

    // Additional endpoints as needed
}