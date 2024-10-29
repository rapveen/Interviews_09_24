package service;

import domain.Customer;
import domain.Rental;

@Service
class AuditService {
    public void logProductAdded(Product product) { /* ... */ }
    public void logProductRemoved(Product product, String reason) { /* ... */ }
    public void logRentalCreated(Rental rental) { /* ... */ }
    public void logCustomerCharged(Customer customer, double amount) { /* ... */ }
}
