package repository;

interface CustomerRepository {
    void add(Customer customer);
    Customer findById(String id);
    List<Customer> findAll();
}
