package dev.aj.spring_6.services;

import dev.aj.spring_6.exceptionHandlers.NotFoundException;
import dev.aj.spring_6.model.BeerDTO;
import dev.aj.spring_6.model.BeerStyle;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BeerServiceJPAIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private BeerService beerService;

    @Test
    @Transactional
    @Rollback
    void getBeerById() {
        BeerDTO newBeerDTO = generateBeerDTO();
        BeerDTO savedBeerDTO = beerService.saveBeer(newBeerDTO);
        BeerDTO foundBeerDTO = beerService.getBeerById(savedBeerDTO.getId());

        Assertions.assertThat(foundBeerDTO).usingRecursiveComparison().isEqualTo(savedBeerDTO);
    }

    private static BeerDTO generateBeerDTO() {
        return BeerDTO.builder()
                .beerStyle(BeerStyle.WHEAT)
                .beerName("Galaxy Cat")
                .upc("283923ds")
                .quantityOnHand(23)
                .price(new BigDecimal("12.34"))
                .build();
    }

    @Test
    void getBeerByIdNotFound() {
        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class, () -> beerService.getBeerById(UUID.randomUUID()));
    }

    @Test
    void getAllBeers() {
        Assertions.assertThat(beerService.getAllBeers()).hasSize(0);
    }

    @Rollback
    @Transactional
    @Test
    @Order(1)
    void saveBeer() {
        BeerDTO newBeerDTO = generateBeerDTO();
        BeerDTO savedBeerDTO = beerService.saveBeer(newBeerDTO);

        Assertions.assertThat(savedBeerDTO.getId()).isNotNull();
    }

    @Test
    @Transactional
    @Rollback
    void updateBeer() {
        BeerDTO newBeerDTO = generateBeerDTO();
        BeerDTO savedBeerDTO = beerService.saveBeer(newBeerDTO);

        savedBeerDTO.setBeerName("Galaxy Cat II");

        beerService.updateBeer(savedBeerDTO.getId(), savedBeerDTO);

        BeerDTO foundBeerDTO = beerService.getBeerById(savedBeerDTO.getId());

        Assertions.assertThat(foundBeerDTO).usingRecursiveComparison().isEqualTo(savedBeerDTO);
    }

    @Test
    @Transactional
    @Rollback
    void deleteBeerById() {
        BeerDTO newBeerDTO = generateBeerDTO();
        BeerDTO savedBeerDTO = beerService.saveBeer(newBeerDTO);
        beerService.deleteBeerById(savedBeerDTO.getId());
        org.junit.jupiter.api.Assertions.assertThrows(NotFoundException.class, () -> beerService.getBeerById(savedBeerDTO.getId()));
    }

    @Test
    @Transactional
    @Rollback
    void patchBeerById() {
        BeerDTO newBeerDTO = generateBeerDTO();
        BeerDTO savedBeerDTO = beerService.saveBeer(newBeerDTO);

        savedBeerDTO.setBeerName("Galaxy Cat II");

        beerService.updateBeer(savedBeerDTO.getId(), savedBeerDTO);

        BeerDTO foundBeerDTO = beerService.getBeerById(savedBeerDTO.getId());

        Assertions.assertThat(foundBeerDTO).usingRecursiveComparison().isEqualTo(savedBeerDTO);
    }
}