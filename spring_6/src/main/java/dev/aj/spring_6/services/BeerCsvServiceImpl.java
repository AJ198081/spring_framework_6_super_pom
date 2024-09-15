package dev.aj.spring_6.services;

import com.opencsv.bean.CsvToBeanBuilder;
import dev.aj.spring_6.model.BeerCSVRecord;
import dev.aj.spring_6.repositories.BeerCsvRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeerCsvServiceImpl implements BeerCsvService {

    private final BeerCsvRepository repository;

    @Override
    public List<BeerCSVRecord> convertCSV(File file) {
        List<BeerCSVRecord> beerRecords;
        try {
            beerRecords = new CsvToBeanBuilder<BeerCSVRecord>(new FileReader(file))
                    .withType(BeerCSVRecord.class)
                    .build()
                    .parse();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return beerRecords;
    }

    @Override
    public List<BeerCSVRecord> persistBeerRecords(List<BeerCSVRecord> beerCSVRecords) {
        return repository.saveAllAndFlush(beerCSVRecords);
    }

    @Override
    public Iterable<BeerCSVRecord> getAllBeerRecords() {
        return repository.findAll();
    }

    @Override
    public long getBeerCountFromDatabase() {
        return repository.count();
    }

    @Override
    public Iterable<BeerCSVRecord> getAllBeerRecordsByName(String beerName) {
        return repository.findAllByStyleIsLikeIgnoreCase(beerName);
    }

    @Override
    public Page<BeerCSVRecord> getBeerRecordsPageByName(String beerName, Integer pageNumber, Integer pageSize, String sortBy, String direction) {
//        Sort sort = Sort.by(sortBy);
        Sort sort = Sort.by(Sort.Order.asc(sortBy));
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        return repository.findAllByStyleIsLikeIgnoreCase(beerName, pageRequest);
    }

}
