package dev.aj.spring_6.services;

import dev.aj.spring_6.exceptionHandlers.NotFoundException;
import dev.aj.spring_6.model.BeerDTO;
import dev.aj.spring_6.model.entities.Beer;
import dev.aj.spring_6.model.mappers.BeerMapper;
import dev.aj.spring_6.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Primary
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public BeerDTO getBeerById(UUID id) throws NotFoundException {
        Beer requestedBeer = beerRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("BeerDTO with id %s not found", id)));
        return beerMapper.beerToBeerDto(requestedBeer);
    }

    @Override
    public List<BeerDTO> getAllBeers() {
        return beerMapper.beersToBeerDtos(beerRepository.findAll());
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beerDTO) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beerDTO)));
    }

    @Override
    public void updateBeer(UUID id, BeerDTO beerDTO) {
        Beer beerToUpdate = beerMapper.beerDtoToBeer(beerDTO);
        beerToUpdate.setId(id);
        Optional<Beer> optionalBeer = beerRepository.findById(id);

        if (optionalBeer.isPresent()) {
            Beer beerFromRepo = optionalBeer.get();
            beerFromRepo.setBeerName(beerToUpdate.getBeerName());
            beerFromRepo.setPrice(beerToUpdate.getPrice());
            beerFromRepo.setQuantityOnHand(beerToUpdate.getQuantityOnHand());
            beerFromRepo.setUpc(beerToUpdate.getUpc());
            beerFromRepo.setBeerStyle(beerToUpdate.getBeerStyle());
            beerRepository.save(beerFromRepo);
            return;
        }

        beerRepository.save(beerToUpdate);
    }

    @Override
    public void deleteBeerById(UUID id) {
        beerRepository.deleteById(id);
    }

    @Override
    public void patchBeerById(UUID id, BeerDTO beerDTO) {
        Beer matchedBeer = beerRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Beer with id %s not found", id)));

        Beer beerToUpdate = beerMapper.beerDtoToBeer(beerDTO);

        if (StringUtils.isNotBlank(beerToUpdate.getBeerName())) {
            matchedBeer.setBeerName(beerToUpdate.getBeerName());
        }
        if (beerToUpdate.getPrice() != null) {
            matchedBeer.setPrice(beerToUpdate.getPrice());
        }
        if (beerToUpdate.getQuantityOnHand() != null) {
            matchedBeer.setQuantityOnHand(beerToUpdate.getQuantityOnHand());
        }
        if (StringUtils.isNotBlank(beerToUpdate.getUpc())) {
            matchedBeer.setUpc(beerToUpdate.getUpc());
        }
        if (beerToUpdate.getBeerStyle() != null) {
            matchedBeer.setBeerStyle(beerToUpdate.getBeerStyle());
        }
        beerRepository.save(matchedBeer);
    }
}
