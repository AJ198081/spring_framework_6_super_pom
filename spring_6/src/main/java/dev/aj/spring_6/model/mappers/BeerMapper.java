package dev.aj.spring_6.model.mappers;

import dev.aj.spring_6.model.BeerDTO;
import dev.aj.spring_6.model.entities.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BeerMapper {
    @Mapping(target = "updateDateTime", expression = "java(java.time.ZonedDateTime.ofInstant(beer.getUpdatedTime(), java.time.ZoneId.systemDefault()))")
    @Mapping(target = "createdDateTime", expression = "java(java.time.ZonedDateTime.ofInstant(beer.getCreatedTime(), java.time.ZoneId.systemDefault()))")
    BeerDTO beerToBeerDto(Beer beer);

    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    Beer beerDtoToBeer(BeerDTO beerDTO);

    List<BeerDTO> beersToBeerDtos(List<Beer> beers);
}
