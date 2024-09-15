package dev.aj.spring_6.repositories;

import dev.aj.spring_6.exceptionHandlers.NotFoundException;
import dev.aj.spring_6.model.BeerStyle;
import dev.aj.spring_6.model.entities.Beer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Testcontainers
@ComponentScan("dev.aj.spring_framework_6.config.CustomJpaAuditing")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BeerRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    BeerRepository beerRepository;

    @Test
    void testBeerPersistence() {
        Beer savedTestBeer = beerRepository.save(Beer.builder()
                .beerName("test beer")
                .price(new BigDecimal("12.34"))
                .upc("12343k98")
                .beerStyle(BeerStyle.WHEAT)
                .build());

        beerRepository.flush();

        assertThat(savedTestBeer.getId()).isNotNull();
        assertThat(savedTestBeer.getBeerName()).isEqualTo("test beer");
        assertThat(savedTestBeer.getBeerStyle()).isEqualTo(BeerStyle.WHEAT);

        Beer retrievedBeer = beerRepository.findById(savedTestBeer.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Beer ID %s not found", savedTestBeer.getId())));

        retrievedBeer.setBeerName("updated beer");

        Beer updatedBeer = beerRepository.saveAndFlush(retrievedBeer);

        assertThat(updatedBeer.getCreatedTime()).isEqualTo(savedTestBeer.getCreatedTime());
        assertThat(updatedBeer.getUpdatedTime()).isEqualTo(savedTestBeer.getUpdatedTime());
    }

    @Test
    void testBeerPersistenceFailsWhenConstraintsViolated() {
        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> beerRepository.saveAndFlush(Beer.builder()
                        .beerName("test beer is too long to persist, characters exceeding 20")
                        .price(new BigDecimal("12.34"))
                        .upc("12343k98")
                        .beerStyle(BeerStyle.WHEAT)
                        .build()));
    }
}