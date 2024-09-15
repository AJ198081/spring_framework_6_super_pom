package dev.aj.spring_6.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.aj.spring_6.exceptionHandlers.NotFoundException;
import dev.aj.spring_6.model.BeerDTO;
import dev.aj.spring_6.model.BeerStyle;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {
    private static final Map<UUID, BeerDTO> beerMap = new HashMap<>();

    private final ObjectMapper objectMapper;

    @PostConstruct
    private static void beerMap() {
        BeerDTO beerDTO0 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerStyle(BeerStyle.LAGER)
                .beerName("Galaxy Cat")
                .upc("238923")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(21)
                .createdDateTime(ZonedDateTime.now().minusDays(10))
                .updateDateTime(ZonedDateTime.now().minusHours(10))
                .build();
        BeerDTO beerDTO1 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerStyle(BeerStyle.IPA)
                .beerName("Space Hoppy")
                .upc("492310")
                .price(new BigDecimal("9.99"))
                .quantityOnHand(43)
                .createdDateTime(ZonedDateTime.now().minusDays(15))
                .updateDateTime(ZonedDateTime.now().minusHours(8))
                .build();
        BeerDTO beerDTO2 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerStyle(BeerStyle.PALE_ALE)
                .beerName("Sunset Ale")
                .upc("593742")
                .price(new BigDecimal("11.49"))
                .quantityOnHand(30)
                .createdDateTime(ZonedDateTime.now().minusDays(5))
                .updateDateTime(ZonedDateTime.now().minusHours(3))
                .build();
        BeerDTO beerDTO3 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerStyle(BeerStyle.STOUT)
                .beerName("Lunar Darkness")
                .upc("678345")
                .price(new BigDecimal("13.29"))
                .quantityOnHand(50)
                .createdDateTime(ZonedDateTime.now().minusDays(20))
                .updateDateTime(ZonedDateTime.now().minusHours(5))
                .build();
        BeerDTO beerDTO4 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerStyle(BeerStyle.PILSNER)
                .beerName("Golden Brew")
                .upc("729483")
                .price(new BigDecimal("10.79"))
                .quantityOnHand(28)
                .createdDateTime(ZonedDateTime.now().minusDays(8))
                .updateDateTime(ZonedDateTime.now().minusHours(7))
                .build();
        BeerDTO beerDTO5 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerStyle(BeerStyle.WHEAT)
                .beerName("Summer Wave")
                .upc("849572")
                .price(new BigDecimal("8.99"))
                .quantityOnHand(35)
                .createdDateTime(ZonedDateTime.now().minusDays(12))
                .updateDateTime(ZonedDateTime.now().minusHours(9))
                .build();

        beerMap.put(beerDTO0.getId(), beerDTO0);
        beerMap.put(beerDTO1.getId(), beerDTO1);
        beerMap.put(beerDTO2.getId(), beerDTO2);
        beerMap.put(beerDTO3.getId(), beerDTO3);
        beerMap.put(beerDTO4.getId(), beerDTO4);
        beerMap.put(beerDTO5.getId(), beerDTO5);
    }

    @Override
    public BeerDTO getBeerById(UUID id) throws NotFoundException {
        log.debug(" Get beer by id {}", id.toString());

        if (!beerMap.containsKey(id)) {
            throw new NotFoundException(String.format("BeerDTO with id %s not found", id));
        }

        return beerMap.get(id);
    }

    @Override
    public List<BeerDTO> getAllBeers() {
        return beerMap.values().stream().toList();
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beerDTO) {
        beerMap.put(beerDTO.getId(), beerDTO);
        return beerMap.get(beerDTO.getId());
    }

    @SneakyThrows
    @Override
    public void updateBeer(UUID id, BeerDTO beerDTO) {
        beerMap.put(id, beerDTO);
        log.debug("BeerDTO updated - {}", objectMapper.writeValueAsString(beerMap.get(id)));
    }

    @Override
    public void deleteBeerById(UUID id) {
        beerMap.remove(id);
    }

    @Override
    public void patchBeerById(UUID id, BeerDTO beerDTO) {
        BeerDTO existingBeerDTO = beerMap.get(id);

        existingBeerDTO.setVersion(beerDTO.getVersion() + 1);

        if (beerDTO.getBeerName() != null && StringUtils.hasText(beerDTO.getBeerName())) {
            existingBeerDTO.setBeerName(beerDTO.getBeerName());
        }
        if (StringUtils.hasText(String.valueOf(beerDTO.getBeerStyle()))) {
            existingBeerDTO.setBeerStyle(beerDTO.getBeerStyle());
        }

        if (beerDTO.getPrice() != null) {
            existingBeerDTO.setPrice(beerDTO.getPrice());
        }

        if (beerDTO.getQuantityOnHand() != 0) {
            existingBeerDTO.setQuantityOnHand(beerDTO.getQuantityOnHand());
        }

        if (beerDTO.getUpc() != null) {
            existingBeerDTO.setUpc(beerDTO.getUpc());
        }

        if (beerDTO.getCreatedDateTime() != null) {
            existingBeerDTO.setCreatedDateTime(beerDTO.getCreatedDateTime());
        }

        if (beerDTO.getUpdateDateTime() != null) {
            existingBeerDTO.setUpdateDateTime(beerDTO.getUpdateDateTime());
        }
    }
}
