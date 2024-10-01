package dev.aj.spring_6.repositories;

import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import dev.aj.spring_6.model.BeerStyle;
import dev.aj.spring_6.model.entities.Beer;
import dev.aj.spring_6.model.entities.Category;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withUsername("admin")
            .withPassword("password")
            .withDatabaseName("postgres_tc")
            .withExposedPorts(5432)
            .withCreateContainerCmdModifier(cmd ->
                            cmd.withHostConfig(
                                    new HostConfig()
//                                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(5434),new ExposedPort(5432))))
                                            //Pay attention to port-binding, it is the host port and exposed port
                                            .withPortBindings(PortBinding.parse("5434:5432")))
            )
            .withReuse(true);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BeerRepository beerRepository;

    private Beer firstBeer;

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgreSQLContainer).join();
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        firstBeer = beerRepository.findAll().stream()
                .findFirst()
                .orElse(beerRepository.save(
                                Beer.builder()
                                        .upc("2398ujifo")
                                        .beerName("first beer")
                                        .beerStyle(BeerStyle.IPA)
                                        .price(new BigDecimal("23.99"))
                                        .build()
                        )
                );

        beerRepository.flush();
    }

    @Test
    void initiallyNOBeersExistInDB() {
        List<Beer> allBeers = beerRepository.findAll();
        Assertions.assertThat(allBeers).isNotNull();
    }

    @Test
    @Transactional
    void testAddCategory() {
        Category ipa = Category.builder()
                .description("IPA")
                .build();

//        categoryRepository.flush();

        Beer initialBeer = beerRepository.findAll().stream()
                .findFirst()
                .orElse(beerRepository.save(
                                Beer.builder()
                                        .upc("2398ujifo")
                                        .beerName("first beer")
                                        .beerStyle(BeerStyle.IPA)
                                        .price(new BigDecimal("23.99"))
                                        .build()
                        )
                );
        initialBeer.addCategory(ipa);

//        Beer savedBeer = beerRepository.save(initialBeer);
//        beerRepository.flush();

        Assertions.assertThat(initialBeer.getCategories()).hasSize(2);
        Assertions.assertThat(initialBeer.getCategories().stream().findFirst().get().getDescription()).isEqualTo("IPA");

    }
}