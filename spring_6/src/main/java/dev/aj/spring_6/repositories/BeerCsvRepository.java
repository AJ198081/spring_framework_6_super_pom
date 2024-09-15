package dev.aj.spring_6.repositories;

import dev.aj.spring_6.model.BeerCSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerCsvRepository extends JpaRepository<BeerCSVRecord, Integer> {
    Iterable<BeerCSVRecord> findAllByStyleIsLikeIgnoreCase(String beerName);

    Page<BeerCSVRecord> findAllByStyleIsLikeIgnoreCase(String beerName, Pageable pageable);
}

