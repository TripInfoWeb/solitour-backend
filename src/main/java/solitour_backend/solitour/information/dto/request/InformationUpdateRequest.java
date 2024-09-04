package solitour_backend.solitour.information.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.image.dto.request.ImageDeleteRequest;
import solitour_backend.solitour.place.dto.request.PlaceModifyRequest;
import solitour_backend.solitour.tag.dto.request.TagRegisterRequest;

import java.util.List;

@Getter
@NoArgsConstructor
public class InformationUpdateRequest {

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;

    @NotBlank
    @Size(min = 1, max = 20)
    private String address;

    private String content;

    private String tips;

    @NotNull
    private PlaceModifyRequest placeModifyRequest;

    @NotNull
    @Min(1)
    private Long categoryId;

    @NotBlank
    @Size(min = 1, max = 20)
    private String zoneCategoryNameParent;

    @NotBlank
    @Size(min = 1, max = 20)
    private String zoneCategoryNameChild;

    private List<ImageDeleteRequest> deleteImages;

    @Size(min = 0, max = 200)
    private String thumbNailUrl;

    private List<String> contentImagesUrl;

    private List<TagRegisterRequest> tagRegisterRequests;
}
