package dev.aj.applicationevents.order;

import dev.aj.applicationevents.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final ApplicationEventPublisher publisher;
    private final CustomerRepository customerRepository;

    @Transactional
    public void createOrder(Order order) {
        log.info("Placing order for = {}", order.getCustomer().getFirstName());
        order.setStatus(OrderStatus.COMPLETED);
        Order savedOrder = repository.save(order);

        log.info("Publishing OrderEvent for order ID = {}", savedOrder.getId());

        publisher.publishEvent(new OrderCompletedEvent(savedOrder));

        customerRepository.findById(savedOrder.getCustomer().getId()).ifPresent(customer -> {
            customer.setRewardsPoints(savedOrder.getCustomer().getRewardsPoints() + 10);
            customerRepository.save(customer);
        });
    }

    @Bean
    public Predicate<Order> hasOrderCompleted() {
        return order -> order.getStatus().equals(OrderStatus.COMPLETED);
    }


    public record OrderCompletedEvent(Order order) {
    }

}
