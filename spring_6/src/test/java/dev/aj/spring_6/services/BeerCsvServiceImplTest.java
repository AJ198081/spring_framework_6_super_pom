package dev.aj.spring_6.services;

import dev.aj.spring_6.model.BeerCSVRecord;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.util.List;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
//        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.show_sql=false",
        "logging.level.org.hibernate.orm.jdbc.bind=false"
})
class BeerCsvServiceImplTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    BeerCsvService beerCsvService;

    @SneakyThrows
    @Test
    void convertCSV() {
        File file = ResourceUtils.getFile("classpath:beers.csv");

        List<BeerCSVRecord> beerCSVRecords = beerCsvService.convertCSV(file);

        Assertions.assertThat(beerCSVRecords).isNotEmpty();
        Assertions.assertThat(beerCSVRecords).hasSize(2410);
    }

    @SneakyThrows
    @Test
    void testBeersPersistedToDB() {
        File file = ResourceUtils.getFile("classpath:beers.csv");

        List<BeerCSVRecord> beerCSVRecords = beerCsvService.convertCSV(file);

        List<BeerCSVRecord> csvRecords = beerCsvService.persistBeerRecords(beerCSVRecords);

        Assertions.assertThat(csvRecords.size()).isEqualTo(2410);

        Assertions.assertThat(beerCsvService.getBeerCountFromDatabase()).isEqualTo(2410);
    }

    @SneakyThrows
    @Test
    void testFetchBeerByName() {
        beerCsvService.persistBeerRecords(
                beerCsvService.convertCSV(
                        ResourceUtils.getFile("classpath:beers.csv")));

        Iterable<BeerCSVRecord> ciders = beerCsvService.getAllBeerRecordsByName("Cider");
        Assertions.assertThat(ciders).hasSize(37);

        Iterable<BeerCSVRecord> wildCardCiders = beerCsvService.getAllBeerRecordsByName("%" + "Wheat" + "%");
        Assertions.assertThat(wildCardCiders).hasSize(105);

        Page<BeerCSVRecord> beerPage = beerCsvService.getBeerRecordsPageByName("Cider", 0, 25, "beer", "asc");
        Assertions.assertThat(beerPage.getContent().size()).isEqualTo(25);

        Page<BeerCSVRecord> beerLastPage = beerCsvService.getBeerRecordsPageByName("Cider", 1, 25, "beer", "asc");
        Assertions.assertThat(beerLastPage.getContent().size()).isEqualTo(12);

    }


}