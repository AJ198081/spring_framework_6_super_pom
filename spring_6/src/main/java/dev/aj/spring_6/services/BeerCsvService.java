package dev.aj.spring_6.services;

import dev.aj.spring_6.model.BeerCSVRecord;
import org.springframework.data.domain.Page;

import java.io.File;
import java.util.List;

public interface BeerCsvService {
    List<BeerCSVRecord> convertCSV(File file);

    List<BeerCSVRecord> persistBeerRecords(List<BeerCSVRecord> beerCSVRecord);

    Iterable<BeerCSVRecord> getAllBeerRecords();

    long getBeerCountFromDatabase();

    Iterable<BeerCSVRecord> getAllBeerRecordsByName(String beerName);

    Page<BeerCSVRecord> getBeerRecordsPageByName(String beerName, Integer pageNumber, Integer pageSize, String sortBy, String direction);
}
