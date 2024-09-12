package solitour_backend.solitour.information.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.place.dto.request.PlaceRegisterRequest;
import solitour_backend.solitour.tag.dto.request.TagRegisterRequest;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InformationCreateRequest {
    @NotBlank
    @Size(min = 1, max = 50)
    private String informationTitle;

    @NotBlank
    @Size(min = 1, max = 50)
    private String informationAddress;

    private String informationContent;

    private String informationTips;

    @NotNull
    private PlaceRegisterRequest placeRegisterRequest;

    @NotNull
    @Min(1)
    private Long categoryId;

    @NotBlank
    @Size(min = 1, max = 20)
    private String zoneCategoryNameParent;

    @NotBlank
    @Size(min = 1, max = 20)
    private String zoneCategoryNameChild;

    @NotBlank
    private String thumbNailImageUrl;

    private List<String> contentImagesUrl;

    private List<TagRegisterRequest> tagRegisterRequests;
}
