package dev.aj.applicationevents.email;

import dev.aj.applicationevents.customer.CustomerRegisteredEvent;
import dev.aj.applicationevents.customer.CustomerRemovedEvent;
import dev.aj.applicationevents.order.Order;
import dev.aj.applicationevents.order.OrderService;
import dev.aj.applicationevents.promotions.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailListeners {

    public static final String CUSTOMER_REMOVED = "Customer removed";
    public static final String CUSTOMER_REGISTERED = "Customer registered";
    private final EmailService emailService;


    @EventListener
    public void onCustomerRegistered(CustomerRegisteredEvent event) {
        Email email = Email.builder()
                .toEmail(event.getCustomer().getEmail())
                .fromEmail("myemail@gmail.com")
                .subject(CUSTOMER_REGISTERED)
                .body(event.getCustomer().getFirstName().concat(event.getCustomer().getLastName()).concat(" has been registered"))
                .build();

        Email sentEmail = emailService.sendEmail(email);
        log.info("Registeration email sent to customer = {}", sentEmail);
    }

    @EventListener
    public void onCustomerRemoved(CustomerRemovedEvent event) {
        Email cutomerRemovedEmail = Email.builder()
                .toEmail(event.customer().getEmail())
                .fromEmail("myemail@gmail.com")
                .subject(CUSTOMER_REMOVED)
                .body(event.customer().getFirstName().concat(event.customer().getLastName()).concat(" has been removed"))
                .build();

        Email sentEmail = emailService.sendEmail(cutomerRemovedEmail);
        log.info("De-registration email sent to customer = {}", sentEmail);
    }

    @EventListener(condition = "#event.order() == null")
    @Async
    public void onPromotionEvent(PromotionService.PromotionEvent event) {
        Email email = Email.builder()
                .toEmail(event.email())
                .subject(event.message())
                .build();
        emailService.sendEmail(email);
        log.info("Newsletter emailed to = {}", email.getToEmail());
    }

    @TransactionalEventListener(condition = "#event.order() != null", phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void onRewardsPointsAward(PromotionService.PromotionEvent event) {

        Email email = Email.builder()
                .toEmail(event.email())
                .subject(event.message())
                .build();
        emailService.sendEmail(email);
        log.info("Rewards updates emailed to = {}, total points = {}", event.customer().getFirstName(), event.customer().getRewardsPoints());
    }

    @EventListener(condition = "@orderService.hasOrderCompleted().test(#event.order)")
//    @Async
    public void onOrderCompletedEvent(OrderService.OrderCompletedEvent event) {
        Order order = event.order();

        Email email = Email.builder()
                .toEmail(order.getCustomer().getEmail())
                .fromEmail("noreply@company.com")
                .subject("Order Completed")
                .body("Your order with ID " + order.getId() + " has been completed.")
                .build();

        emailService.sendEmail(email);
        log.info("Order completed email forwarded to = {}", email.getToEmail());
    }
}
