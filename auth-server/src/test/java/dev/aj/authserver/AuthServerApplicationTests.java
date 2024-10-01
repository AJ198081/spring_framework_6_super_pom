package dev.aj.authserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(value = {TestcontainersConfiguration.class})
@SpringBootTest
class AuthServerApplicationTests {

    @Test
    void contextLoads() {
    }

}
