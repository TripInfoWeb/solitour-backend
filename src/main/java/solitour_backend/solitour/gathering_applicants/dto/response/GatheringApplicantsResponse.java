package solitour_backend.solitour.gathering_applicants.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.gathering_applicants.entity.GatheringStatus;
import solitour_backend.solitour.user.dto.response.UserGatheringResponse;

@Getter
@AllArgsConstructor
public class GatheringApplicantsResponse {
    private UserGatheringResponse userGatheringResponse;
    private GatheringStatus gatheringStatus;
}
