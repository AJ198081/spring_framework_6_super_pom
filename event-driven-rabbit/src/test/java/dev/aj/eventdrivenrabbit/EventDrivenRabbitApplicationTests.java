package dev.aj.eventdrivenrabbit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class EventDrivenRabbitApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("Context Loaded");
    }

}
