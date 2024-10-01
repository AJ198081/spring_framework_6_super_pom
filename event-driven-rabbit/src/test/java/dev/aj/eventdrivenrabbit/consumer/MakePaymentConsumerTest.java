package dev.aj.eventdrivenrabbit.consumer;

import dev.aj.eventdrivenrabbit.TestcontainersConfiguration;
import dev.aj.eventdrivenrabbit.domain.model.Payment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MakePaymentConsumerTest {

    @Autowired
    MakePaymentConsumer makePaymentConsumer;

    @Test
    void accept() {
        makePaymentConsumer.accept(Payment.builder()
                .id(UUID.randomUUID().toString())
                .amount(BigDecimal.TEN)
                .build());
    }
}