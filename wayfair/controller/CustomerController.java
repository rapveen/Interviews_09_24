package controller;

import com.rental.domain.Customer;
import com.rental.dto.CustomerBalance;
import com.rental.dto.CustomerResponse;
import com.rental.service.RentalService;
import com.rental.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final RentalService rentalService;

    public CustomerController(CustomerService customerService, RentalService rentalService) {
        this.customerService = customerService;
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> addCustomer(@RequestBody CustomerRequest request) {
        Customer customer = customerService.addCustomer(request);
        CustomerResponse response = CustomerResponse.fromCustomer(customer);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/balance")
    public ResponseEntity<CustomerBalance> getCustomerBalance(@PathVariable String customerId) {
        double balance = rentalService.getCustomerBalance(customerId);
        CustomerBalance response = new CustomerBalance(customerId, balance);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}/rentals")
    public ResponseEntity<List<RentalInfo>> getCustomerRentals(@PathVariable String customerId) {
        List<RentalInfo> rentals = rentalService.getCustomerRentals(customerId);
        return ResponseEntity.ok(rentals);
    }

    // Additional endpoints as needed
}