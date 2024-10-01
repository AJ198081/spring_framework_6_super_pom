package dev.aj.eventdrivenrabbit;

import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
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

    @Bean
    @ServiceConnection
    RabbitMQContainer rabbitContainer() {
        return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management"))
                .withReuse(true)
                .withAdminPassword("guest")
                .withAdminPassword("password")
                .withCreateContainerCmdModifier(createContainerCmd -> createContainerCmd
                                .withName("rabbit-test-container")
                                /*.withHostConfig(HostConfig.newHostConfig()
                                        .withPortBindings(PortBinding.parse("15672:15672")
                                ))*/
//                                .withPortSpecs("15672:15672")
                     /*   .withHostConfig(new HostConfig()
                                .withPortBindings(PortBinding.parse("15672:15672")))*/
                )
   /*             .withCreateContainerCmdModifier(createContainerCmd -> createContainerCmd
                        .withHostConfig(new HostConfig()
                                .withPortBindings(PortBinding.parse("15672:15672"))))*/
                ;
    }

    @Bean
    @ServiceConnection(name = "redis")
    RedisContainer redisContainer() {
        return new RedisContainer(DockerImageName.parse("redis:latest"))
                .withReuse(true)
                .withCreateContainerCmdModifier(createContainerCmd -> {
                    createContainerCmd
                            .withName("redis-test-container")
                            .withHostConfig(new HostConfig()
                                    .withPortBindings(PortBinding.parse("6379:6379")));
                });
    }

}
