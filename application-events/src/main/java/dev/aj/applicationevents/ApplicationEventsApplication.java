package dev.aj.applicationevents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ApplicationEventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationEventsApplication.class, args);
    }

}
