package dev.aj.eventdrivenrabbit.function;

import dev.aj.eventdrivenrabbit.domain.model.Balance;
import dev.aj.eventdrivenrabbit.domain.model.Payment;
import dev.aj.eventdrivenrabbit.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer implements Consumer<Payment> {

    private final BalanceRepository repository;

    @Override
    public void accept(Payment payment) {
        repository.save(calculateNewBalance(payment));
    }

    Balance calculateNewBalance(Payment payment) {
        log.info("Received payment: {}", payment);
        Balance currentBalance = repository.findById(payment.getId()).orElse(new Balance(payment.getId(), BigDecimal.ZERO));
        log.info("Current balance: {}", currentBalance);
        return new Balance(currentBalance.getId(), currentBalance.getAmount().add(payment.getAmount()));
    }
}
