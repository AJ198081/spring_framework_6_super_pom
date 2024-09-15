package dev.aj.spring_6.controller;

import dev.aj.spring_6.exceptionHandlers.NotFoundException;
import dev.aj.spring_6.model.BeerCSVRecord;
import dev.aj.spring_6.model.BeerDTO;
import dev.aj.spring_6.services.BeerCsvService;
import dev.aj.spring_6.services.BeerService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/v1/beer", produces = "application/json")
public class BeerController {

    private final BeerService beerService;
    private final BeerCsvService beerCsvService;

    @GetMapping("/{id}")
    public ResponseEntity<BeerDTO> getBeerById(@PathVariable UUID id) {
        BeerDTO beerDTO = beerService.getBeerById(id);
        return ResponseEntity.ok(beerDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<BeerDTO>> getAllBeers() {
        return ResponseEntity.ok(beerService.getAllBeers());
    }

    @GetMapping("/all/csv")
    public ResponseEntity<Iterable<BeerCSVRecord>> getAllBeersAsCsv(
            @RequestParam(name = "beerName", required = false) String beerName,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        Iterable<BeerCSVRecord> beerCSVRecords;
        if (StringUtils.isNotBlank(beerName)) {
            beerCSVRecords = beerCsvService.getAllBeerRecordsByName(beerName);
        } else {
            beerCSVRecords = beerCsvService.getAllBeerRecords();
        }
        return ResponseEntity.ok(beerCsvService.getAllBeerRecords());
    }

    @SneakyThrows
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> saveBeer(@RequestBody @Validated BeerDTO beerDTO, HttpServletRequest request) {
        BeerDTO savedBeer = beerService.saveBeer(beerDTO);

        URI uri = UriComponentsBuilder
                .fromUriString(request.getRequestURI())
                .path("/".concat(String.valueOf(savedBeer.getId())))
                .build()
                .toUri();

        return ResponseEntity.created(uri).build();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> updateBeer(@PathVariable UUID id, @RequestBody BeerDTO beerDTO) {
        beerService.updateBeer(id, beerDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBeer(@PathVariable UUID id) {
        beerService.deleteBeerById(id);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> patchBeer(@PathVariable UUID id, @RequestBody BeerDTO beerDTO) {
        beerService.patchBeerById(id, beerDTO);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NotFoundException.class) //Overrides Controller Advice, global exception handlers
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), NOT_FOUND);
    }
}
