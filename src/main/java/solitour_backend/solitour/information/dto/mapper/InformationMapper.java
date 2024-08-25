package solitour_backend.solitour.information.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.information.dto.response.InformationResponse;
import solitour_backend.solitour.information.entity.Information;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface InformationMapper {

    InformationResponse mapToInformationResponse(Information information);
}
