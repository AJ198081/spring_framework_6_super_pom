package dev.aj.spring_6.model;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "beer_csv_record")
public class BeerCSVRecord {
    @CsvBindByName(column = "id")
    @Id
    private Integer id;

    @CsvBindByName(column = "row")
    private Integer row;

    @CsvBindByName(column = "count.x")
    private Integer count;

    @CsvBindByName(column = "abv")
    private String abv;

    @CsvBindByName(column = "ibu")
    private String ibu;

    @CsvBindByName(column = "beer")
    private String beer;

    @CsvBindByName(column = "style")
    private String style;

    @CsvBindByName(column = "brewery_id")
    private Integer breweryId;

    @CsvBindByName(column = "ounces")
    private Float ounces;

    @CsvBindByName(column = "style2")
    private String style2;

    @CsvBindByName(column = "count.y")
    private String count_y;

    @CsvBindByName(column = "brewery")
    private String brewery;

    @CsvBindByName(column = "city")
    private String city;

    @CsvBindByName(column = "state")
    private String state;

    @CsvBindByName(column = "label")
    private String label;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant createdTime;

    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMPTZ", insertable = false)
    private Instant updatedTime;
}
