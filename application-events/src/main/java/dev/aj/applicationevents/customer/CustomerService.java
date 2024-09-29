package dev.aj.applicationevents.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final ApplicationEventPublisher publisher;

    public void registerCustomer(Customer customer) {
        Customer registerdCustomer = repository.save(customer);
        publisher.publishEvent(new CustomerRegisteredEvent(registerdCustomer));
    }

    public void removeCustomer(Customer customer) {
        repository.delete(customer);
    }

    public void removeCustomerById(Long id) {
        Customer customer = repository.findById(id).orElseThrow();
        this.removeCustomer(customer);
        publisher.publishEvent(new CustomerRemovedEvent(customer));
    }

}
