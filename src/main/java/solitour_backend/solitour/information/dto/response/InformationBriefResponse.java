package solitour_backend.solitour.information.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InformationBriefResponse {
    private Long informationId;
    private String title;
    private String zoneCategoryParentName;
    private String zoneCategoryChildName;
    private Integer viewCount;
    private Boolean isBookMark;
    private String thumbNailImage;
    private Integer likeCount;
}