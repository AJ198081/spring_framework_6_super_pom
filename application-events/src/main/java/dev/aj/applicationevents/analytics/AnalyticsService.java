package dev.aj.applicationevents.analytics;

import dev.aj.applicationevents.customer.CustomerRegisteredEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnalyticsService {

    @SneakyThrows
    public void registeredNewCustomer(CustomerRegisteredEvent event) {

        Thread.sleep(100);

        log.info("Registered {} {} and email has been sent to {}",
                event.getCustomer().getFirstName(),
                event.getCustomer().getLastName(),
                event.getCustomer().getEmail());

    }
}
