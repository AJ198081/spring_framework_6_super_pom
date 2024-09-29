package dev.aj.applicationevents.analytics;

import dev.aj.applicationevents.customer.CustomerRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalyticsListener {

    private final AnalyticsService service;

    @EventListener
    @Async
    public void onRegistration(CustomerRegisteredEvent event) {
        service.registeredNewCustomer(event);
    }

}
