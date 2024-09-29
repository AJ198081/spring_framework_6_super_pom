package dev.aj.authclient.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator customRouteLocation(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("get-all-beers", predicateSpec -> predicateSpec
                        .path("/api/v1/*")
                        .filters(tokenRelayFilterSpec -> tokenRelayFilterSpec.tokenRelay("oidc-client"))
                        .uri("http://localhost:8080/api/v1/beer/all"))
                .route("redirect-from-auth-server", predicateSpec -> predicateSpec
                        .path("/login/oauth2/oidc-provider/code")
                        .filters(tokenRelayFilterSpec -> tokenRelayFilterSpec.tokenRelay("oidc-client"))
                        .uri("http://localhost:8080/api/v1/beer/all"))
                .build();
    }

}