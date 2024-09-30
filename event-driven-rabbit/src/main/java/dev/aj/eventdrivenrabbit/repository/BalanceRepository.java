package dev.aj.eventdrivenrabbit.repository;

import dev.aj.eventdrivenrabbit.domain.model.Balance;
import org.springframework.data.repository.CrudRepository;

public interface BalanceRepository extends CrudRepository<Balance, String> {
}
