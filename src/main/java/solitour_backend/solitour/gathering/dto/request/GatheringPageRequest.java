package solitour_backend.solitour.gathering.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import solitour_backend.solitour.gathering.entity.AllowedSex;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GatheringPageRequest {
    private Boolean isExclude;

    @Min(1)
    private Long category;

    @Min(1)
    private Long location;

    private AllowedSex allowedSex;

    @Min(20)
    private Integer startAge;

    @Min(20)
    private Integer endAge;

    private String sort;

    private String search;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
