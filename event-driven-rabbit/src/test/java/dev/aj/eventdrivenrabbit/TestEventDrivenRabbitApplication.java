package dev.aj.eventdrivenrabbit;

import org.springframework.boot.SpringApplication;

public class TestEventDrivenRabbitApplication {

    public static void main(String[] args) {
        SpringApplication.from(EventDrivenRabbitApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
