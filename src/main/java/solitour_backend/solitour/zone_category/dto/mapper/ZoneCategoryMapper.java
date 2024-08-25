package solitour_backend.solitour.zone_category.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.zone_category.dto.response.ZoneCategoryResponse;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ZoneCategoryMapper {

    ZoneCategoryResponse mapToZoneCategoryResponse(ZoneCategory zoneCategory);
}
