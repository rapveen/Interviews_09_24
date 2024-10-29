package service;

import domain.Customer;
import domain.Rental;

@Service
class NotificationService {
    public void sendRentalConfirmation(Rental rental) { /* ... */ }
    public void sendChargeNotification(Customer customer, double amount) { /* ... */ }
}
