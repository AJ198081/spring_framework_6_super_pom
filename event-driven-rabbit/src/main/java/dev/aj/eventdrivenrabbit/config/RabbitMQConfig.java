package dev.aj.eventdrivenrabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value( "${spring.rabbitmq.queue.payment:payment-queue}")
    public String paymentQueue;

    @Value("${spring.rabbitmq.exchange.destination:payment-exchange}")
    private String exchange;

    @Bean
    public Queue paymentQueue() {
        return new Queue(paymentQueue, true);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Binding binding(Queue paymentQueue, DirectExchange paymentExchange) {
        return BindingBuilder.bind(paymentQueue)
                .to(paymentExchange)
                .with("#");
    }

}
