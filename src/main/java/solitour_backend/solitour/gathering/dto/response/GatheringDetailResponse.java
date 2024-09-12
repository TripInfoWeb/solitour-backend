package solitour_backend.solitour.gathering.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.gathering.entity.AllowedSex;
import solitour_backend.solitour.gathering_applicants.dto.response.GatheringApplicantsResponse;
import solitour_backend.solitour.gathering_applicants.entity.GatheringStatus;
import solitour_backend.solitour.gathering_category.dto.response.GatheringCategoryResponse;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.tag.dto.response.TagResponse;
import solitour_backend.solitour.user.dto.UserPostingResponse;
import solitour_backend.solitour.zone_category.dto.response.ZoneCategoryResponse;

@Getter
@AllArgsConstructor
public class GatheringDetailResponse {
    private String title;
    private String content;
    private Integer personCount;
    private Integer viewCount;
    private LocalDateTime createdAt;

    private LocalDateTime scheduleStartDate;
    private LocalDateTime scheduleEndDate;
    private LocalDateTime deadline;
    private Boolean isFinish;

    private AllowedSex allowedSex;
    private Integer startAge;
    private Integer endAge;

    private List<TagResponse> tagResponses;

    private UserPostingResponse userPostingResponse;
    private PlaceResponse placeResponse;
    private ZoneCategoryResponse zoneCategoryResponse;
    private GatheringCategoryResponse gatheringCategoryResponse;

    private Integer likeCount;
    private Integer nowPersonCount;

    private Boolean isLike;

    private String openChattingUrl;

    private String userImage;

    private List<GatheringApplicantsResponse> gatheringApplicantsResponses;

    private List<GatheringBriefResponse> gatheringRecommend;

    private GatheringStatus gatheringStatus;
}
