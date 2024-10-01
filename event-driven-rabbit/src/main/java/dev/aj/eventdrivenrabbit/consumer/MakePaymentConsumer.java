package dev.aj.eventdrivenrabbit.consumer;

import dev.aj.eventdrivenrabbit.domain.model.Payment;
import dev.aj.eventdrivenrabbit.function.PaymentConsumer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class MakePaymentConsumer implements Consumer<Payment> {

    private final RabbitTemplate rabbitTemplate;
    private final PaymentConsumer paymentConsumer;

    @Value("${spring.rabbitmq.exchange.destination:payment-exchange}")
    private String exchange;

    @SneakyThrows
    @Override
    public void accept(Payment payment) {
        rabbitTemplate.convertAndSend(exchange, payment.getId(), payment);
        paymentConsumer.accept(payment);
    }
}
