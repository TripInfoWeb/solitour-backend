package solitour_backend.solitour.information.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.image.dto.response.ImageResponse;
import solitour_backend.solitour.place.dto.response.PlaceResponse;
import solitour_backend.solitour.tag.dto.response.TagResponse;
import solitour_backend.solitour.user.dto.UserPostingResponse;
import solitour_backend.solitour.zone_category.dto.response.ZoneCategoryResponse;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<ImageResponse> imageResponses;
    private int likeCount;
}
