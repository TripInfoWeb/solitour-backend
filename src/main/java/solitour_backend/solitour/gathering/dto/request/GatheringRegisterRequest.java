package solitour_backend.solitour.gathering.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import solitour_backend.solitour.gathering.entity.AllowedSex;
import solitour_backend.solitour.place.dto.request.PlaceRegisterRequest;
import solitour_backend.solitour.tag.dto.request.TagRegisterRequest;

@Getter
@NoArgsConstructor
public class GatheringRegisterRequest {
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    private String content;

    @NotNull
    @Min(2)
    @Max(10)
    private Integer personCount; //몇명으로 제한 할 것인지

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime scheduleStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime scheduleEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime deadline;

    @NotNull
    private AllowedSex allowedSex;

    @NotNull
    @Min(20)
    private Integer startAge;

    @NotNull
    @Min(20)
    private Integer endAge;

    @NotNull
    private PlaceRegisterRequest placeRegisterRequest;

    @NotNull
    @Min(1)
    private Long gatheringCategoryId;

    @NotBlank
    @Size(min = 1, max = 20)
    private String zoneCategoryNameParent;

    @NotBlank
    @Size(min = 1, max = 20)
    private String zoneCategoryNameChild;

    private List<TagRegisterRequest> tagRegisterRequests;

    @NotBlank
    @Size(min = 1, max = 255)
    private String openChattingUrl;
}
