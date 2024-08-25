package solitour_backend.solitour.gathering_applicants.dto.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.gathering_applicants.dto.response.GatheringApplicantsResponse;
import solitour_backend.solitour.gathering_applicants.entity.GatheringApplicants;
import solitour_backend.solitour.user.dto.mapper.UserMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = UserMapper.class)
public interface GatheringApplicantsMapper {

    @Mapping(source = "user", target = "userGatheringResponse")
    GatheringApplicantsResponse mapToGatheringApplicantsResponse(GatheringApplicants gatheringApplicants);

    List<GatheringApplicantsResponse> mapToGatheringApplicantsResponses(List<GatheringApplicants> gatheringApplicants);
}
