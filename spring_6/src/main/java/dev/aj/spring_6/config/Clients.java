package dev.aj.spring_6.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class Clients {

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {

        String baseUrl = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path(contextPath)
                .toUriString();

        return builder
                .baseUrl(baseUrl)
                .build();
    }
}
