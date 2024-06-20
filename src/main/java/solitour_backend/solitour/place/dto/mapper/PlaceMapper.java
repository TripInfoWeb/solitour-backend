package solitour_backend.solitour.place.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.place.entity.Place;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = BigDecimalMapper.class)
public interface PlaceMapper {

    PlaceResponse mapToPlaceResponse(Place place);
}
