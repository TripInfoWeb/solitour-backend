package solitour_backend.solitour.information.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class InformationBriefResponse {

    private Long informationId;
    private String title;
    private String zoneCategoryParentName;
    private String zoneCategoryChildName;
    private String categoryName;
    private Integer viewCount;
    private Boolean isBookMark;
    private String thumbNailImage;
    private Integer likeCount;
    private Boolean isLike;
}
