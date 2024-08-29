package solitour_backend.solitour.gathering_category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GatheringCategoryModifyRequest {
    @NotBlank
    @Size(min = 2, max = 20)
    private String name;
}
