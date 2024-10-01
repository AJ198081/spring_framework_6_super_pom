package dev.aj.authserver;

import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
                .withReuse(true)
                .withDatabaseName("postgres_tc")
                .withUsername("admin")
                .withPassword("password")
                .withExposedPorts(PostgreSQLContainer.POSTGRESQL_PORT)
                .withCreateContainerCmdModifier(createContainerCmd -> createContainerCmd
                        .withName("postgres-test-container")
                        .withHostConfig(new HostConfig()
                                .withPortBindings(PortBinding.parse("5432:5432")))
                );
    }
}
