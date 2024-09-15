package dev.aj.spring_6.repositories;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import dev.aj.spring_6.model.entities.BeerOrder;
import dev.aj.spring_6.model.entities.Customer;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class BeerOrderRepositoryTest {

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withUsername("admin")
            .withPassword("password")
            .withDatabaseName("postgres_tc")
            .withReuse(true)
            .withExposedPorts(5432)
            .withCreateContainerCmdModifier(cmd -> {
                cmd.withHostConfig(new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(5433), new ExposedPort(5432))));
            });
    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgreSQLContainer).join();
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void testNoBeerOrdersAtStartup() {
        Assertions.assertThat(beerOrderRepository.count()).isEqualTo(0);
    }

    @Transactional
    @Test
    void saveABeerOrder() {

        Customer aj = Customer.builder()
                .name("aj")
                .email("<EMAIL>")
                .build();

        BeerOrder beerOrder = BeerOrder.builder()
                .customerRef(aj)
                .build();

        BeerOrder savedOrder = beerOrderRepository.saveAndFlush(beerOrder);

        Optional<BeerOrder> savedAJOrders = beerOrderRepository.findById(savedOrder.getId());

        Assertions.assertThat(savedAJOrders).isPresent();
        Assertions.assertThat(savedAJOrders.get().getId()).isNotNull();
        Assertions.assertThat(savedAJOrders.get().getCustomerRef()).isEqualTo(aj);
    }
}