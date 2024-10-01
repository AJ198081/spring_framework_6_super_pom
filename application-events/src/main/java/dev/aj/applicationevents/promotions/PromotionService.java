package dev.aj.applicationevents.promotions;

import dev.aj.applicationevents.customer.Customer;
import dev.aj.applicationevents.customer.CustomerRegisteredEvent;
import dev.aj.applicationevents.order.Order;
import dev.aj.applicationevents.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    public final ApplicationEventPublisher publisher;

    @Async
    @EventListener(condition = "#event.customer.acceptedNewsletter")
    public void sendNewLetter(CustomerRegisteredEvent event) {
        log.info("Customer {} will be sent newsletter to {}", event.getCustomer().getFirstName(), event.getCustomer().getEmail());

        String message = String.format("Sending newsletter to %s %s on their registered email %s", event.getCustomer().getFirstName(), event.getCustomer().getLastName(), event.getCustomer().getEmail());

        PromotionEvent promotionEvent = new PromotionEvent(event.getCustomer().getEmail(), message, event.getCustomer(), null);
        publisher.publishEvent(promotionEvent);
    }

    @EventListener
    public void onPromotionEvent(OrderService.OrderCompletedEvent event) {
        Customer customer = event.order().getCustomer();
        publisher.publishEvent(new PromotionEvent(customer.getEmail(), "10 Award points have been added", customer, event.order()));
    }

    public record PromotionEvent(String email, String message, Customer customer, Order order) {
    }
}
