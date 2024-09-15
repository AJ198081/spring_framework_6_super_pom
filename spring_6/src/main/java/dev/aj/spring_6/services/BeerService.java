package dev.aj.spring_6.services;

import dev.aj.spring_6.exceptionHandlers.NotFoundException;
import dev.aj.spring_6.model.BeerDTO;

import java.util.List;
import java.util.UUID;

public interface BeerService {
    BeerDTO getBeerById(UUID id) throws NotFoundException;

    List<BeerDTO> getAllBeers();

    BeerDTO saveBeer(BeerDTO beerDTO);

    void updateBeer(UUID id, BeerDTO beerDTO);

    void deleteBeerById(UUID id);

    void patchBeerById(UUID id, BeerDTO beerDTO);
}
