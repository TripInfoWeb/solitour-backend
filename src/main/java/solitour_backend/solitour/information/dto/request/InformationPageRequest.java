package solitour_backend.solitour.information.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InformationPageRequest {
    @Min(1)
    private Long childCategoryId;

    @Size(min = 1, max = 10)
    private String sort;

    @Min(1)
    private Long zoneCategoryId;

    private String search;
}
