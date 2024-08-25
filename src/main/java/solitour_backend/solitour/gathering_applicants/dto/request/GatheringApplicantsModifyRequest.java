package solitour_backend.solitour.gathering_applicants.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.gathering_applicants.entity.GatheringStatus;

@Getter
@NoArgsConstructor
public class GatheringApplicantsModifyRequest {

    @Min(1)
    @NotNull
    private Long userId;

    @NotNull
    private GatheringStatus gatheringStatus;
}
