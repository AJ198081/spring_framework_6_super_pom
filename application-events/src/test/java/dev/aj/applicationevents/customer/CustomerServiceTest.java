package dev.aj.applicationevents.customer;

import dev.aj.applicationevents.analytics.AnalyticsService;
import dev.aj.applicationevents.email.Email;
import dev.aj.applicationevents.email.EmailRepository;
import dev.aj.applicationevents.order.Order;
import dev.aj.applicationevents.order.OrderRepository;
import dev.aj.applicationevents.order.OrderService;
import dev.aj.applicationevents.ticker.TicketRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static dev.aj.applicationevents.email.EmailListeners.CUSTOMER_REMOVED;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.show_sql=true",
        "spring.jpa.properties.hibernate.format_sql=true",
        "logging.level.org.hibernate.orm.jdbc.bind=trace"
})
@Slf4j
public class CustomerServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private EmailRepository emailRepository;

    @SpyBean
    private AnalyticsService analyticsService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TicketRepository ticketRepository;

    @SneakyThrows
    @Test
    public void shouldRegisterNewCustomer() {
        Customer customer = Customer.builder()
                .email("jd@email.com")
                .firstName("John")
                .lastName("Doe")
                .rewardsPoints(90)
                .acceptedNewsletter(true)
                .build();
        customerService.registerCustomer(customer);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Mockito.verify(analyticsService, Mockito.times(1))
                .registeredNewCustomer(Mockito.any(CustomerRegisteredEvent.class));

        Assertions.assertThat(emailRepository.findAll()).hasSize(2);
        Assertions.assertThat(customerRepository.findAll()).hasSize(1);

        Order order = Order.builder()
                .quantity(10)
                .customer(customer)
                .build();

        try {
            orderService.createOrder(order);
        } catch (Exception e) {
            log.info("Exception with message = {}", e.getMessage());
        }
        Thread.sleep(1000);
        Assertions.assertThat(ticketRepository.findAll()).hasSize(0);
    }

    @Test
    public void shouldDeregisterCustomer() {
        Customer customerToBeRemoved = Customer.builder()
                .email("sw@email.com")
                .firstName("Simon")
                .lastName("White")
                .build();
        Customer savedCustomer = customerRepository.save(customerToBeRemoved);

        customerService.removeCustomerById(savedCustomer.getId());
        Assertions.assertThat(emailRepository.findAll()).hasSize(1);
        Assertions.assertThat(emailRepository.findAll().stream())
                .extracting(Email::getSubject, Email::getToEmail)
                .containsExactly(Tuple.tuple(CUSTOMER_REMOVED, customerToBeRemoved.getEmail()));
        Assertions.assertThat(customerRepository.findAll()).hasSize(0);
    }
}
