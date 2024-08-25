package solitour_backend.solitour.gathering.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.gathering.dto.response.GatheringResponse;
import solitour_backend.solitour.gathering.entity.Gathering;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface GatheringMapper {
    GatheringResponse mapToGatheringResponse(Gathering gathering);
}

