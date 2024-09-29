package dev.aj.spring_6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class Spring6Application {

    public static void main(String[] args) {
        SpringApplication.run(Spring6Application.class, args);
    }

}
