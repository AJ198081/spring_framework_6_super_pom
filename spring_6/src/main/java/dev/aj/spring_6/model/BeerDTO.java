package dev.aj.spring_6.model;

import dev.aj.spring_6.model.entities.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BeerDTO {
    private UUID id;
    private Integer version;
    private String beerName;
    private String batch;
    private Set<Category> categories;
    private BeerStyle beerStyle;
    @NotBlank
    private String upc;

    private Integer quantityOnHand;
    @NotNull
    private BigDecimal price;
    private ZonedDateTime createdDateTime;
    private ZonedDateTime updateDateTime;
}
