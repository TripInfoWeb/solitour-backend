package solitour_backend.solitour.information.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.category.dto.response.CategoryResponse;
import solitour_backend.solitour.image.dto.response.ImageResponse;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.tag.dto.response.TagResponse;
import solitour_backend.solitour.user.dto.UserPostingResponse;
import solitour_backend.solitour.zone_category.dto.response.ZoneCategoryResponse;

@Getter
@AllArgsConstructor
public class InformationDetailResponse {

    private String title;
    private String address;
    private LocalDateTime createdDate;
    private Integer viewCount;
    private String content;
    private String tip;

    private UserPostingResponse userPostingResponse;
    private List<TagResponse> tagResponses;

    private PlaceResponse placeResponse;
    private ZoneCategoryResponse zoneCategoryResponse;
    private CategoryResponse categoryResponse;
    private List<ImageResponse> imageResponses;
    private int likeCount;
    private String userImage;
    private Boolean isLike;
    private List<InformationBriefResponse> recommendInformation;
}
