package dev.aj.spring_6.controller;

import dev.aj.spring_6.model.BeerDTO;
import dev.aj.spring_6.model.BeerStyle;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;


/*
 * You can exclude the SpringSecurity AutoConfiguration, or use MockMvc and requestPostProcessor to add the context and csrf etc
 */

//@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@ContextConfiguration(classes = {SpringFramework6Application.class, BeerControllerTCIT.TestConfig.class})
// Alternatively just import the additional configuration classes only. Spring Context will try to initialise from the normal classes
@Import({BeerControllerTCIT.TestConfig.class})

@TestPropertySource(properties = {
        "server.port=8082",
        "spring.security.user.name=user",
        "spring.security.user.password=password",
        "spring.security.user.roles=ADMIN",
        "logging.level.org.springframework.security.*=trace"
})

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class BeerControllerTCIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

    @Autowired
    private RestClient testRestClient;

    @Test
    void successfullySavesBeer() {

        ResponseEntity<Void> beerResponse = testRestClient.post()
                .uri("/v1/beer")
                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Basic ".concat(Base64.getEncoder().encodeToString("user:password".getBytes(StandardCharsets.UTF_8))))
                .body(BeerDTO.builder()
                        .beerStyle(BeerStyle.WHEAT)
                        .beerName("Test Beer")
                        .upc("283923ds")
                        .price(new BigDecimal("12.34"))
                        .build())
                .retrieve()
                .toBodilessEntity();

        Assertions.assertThat(beerResponse.getStatusCode().is2xxSuccessful()).isTrue();
        Assertions.assertThat(beerResponse.getHeaders().getLocation()).isNotNull();
        Assertions.assertThat(beerResponse.getHeaders().getLocation().isAbsolute()).isFalse();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> {
            UUID.fromString(beerResponse.getHeaders().getLocation().getPath().replace("/api/v1/beer/", ""));
        }, "No valid BeerID received in Location header");
    }

    @Test
    void throwsExceptionWhenSavingInvalidBeer() {
        RestClient.ResponseSpec responseSpec = testRestClient.post()
                .uri("/v1/beer")
                .contentType(MediaType.APPLICATION_JSON)
//                .header("Authorization", "Basic ".concat(Base64.getEncoder().encodeToString("user:password".getBytes(StandardCharsets.UTF_8))))
                .body(BeerDTO.builder()
                        .beerStyle(BeerStyle.WHEAT)
//                        .upc("283923ds")
//                        .price(new BigDecimal("12.34"))
                        .beerName("Test Beer")
                        .build())
                .retrieve();

//        org.junit.jupiter.api.Assertions.assertThrows(HttpClientErrorException.BadRequest.class,
//                () -> responseSpec.toEntity(BeerDTO.class), "Save request should have thrown exception for Invalid Arguments in BeerDtoNo");

        Assertions.assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
                .isThrownBy(() -> responseSpec.toEntity(BeerDTO.class))
                .withMessage("400 : \"[{\"upc\":\"must not be blank\"},{\"price\":\"must not be null\"}]\"");
    }

    @TestConfiguration
    static class TestConfig {


        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            return http
//                    .cors(AbstractHttpConfigurer::disable) // Not need to disable CORS either, all requests are from same origin
//                    Rather than a blanket disable statement you can specify the 'requestMatchers' that shall have csrf ignored
                    .csrf(csrfConfigurer -> csrfConfigurer.ignoringRequestMatchers("/v1/beer"))
                    .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated()) // This is always required, as without 'authenticated', you have basically overridden the default behaviour
                    .httpBasic(Customizer.withDefaults()) // You don't need to have exclusive httpBasic, but it is good to be exclusive
                    .build();
        }

        @Bean
        RestClient testRestClient(
                RestClient.Builder builder,
                @Value("${server.port}") int port,
                @Value("${server.servlet.context-path}") String contextPath,
                @Value("${spring.security.user.name}") String username,
                @Value("${spring.security.user.password}") String password) {

            return builder
                    .baseUrl(UriComponentsBuilder.newInstance()
                            .scheme("http")
                            .host("localhost")
                            .port(port)
                            .path(contextPath).toUriString())
                    .defaultHeader("Authorization", "Basic ".concat(
                            Base64.getEncoder()
                                    .encodeToString(username.concat(":").concat(password)
                                            .getBytes(StandardCharsets.UTF_8))))
                    .build();
        }
    }
}
