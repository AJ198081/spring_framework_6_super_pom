package dev.aj.eventdrivenrabbit.function;

import dev.aj.eventdrivenrabbit.domain.model.Balance;
import dev.aj.eventdrivenrabbit.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class ReadBalanceFunction implements Function<String, Balance> {

    private final BalanceRepository repository;

    @Override
    public Balance apply(String id) {
        return repository.findById(id).orElse(new Balance(id, BigDecimal.ZERO));
    }

}
