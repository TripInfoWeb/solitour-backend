package solitour_backend.solitour.zone_category.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ZoneCategoryModifyRequest {

    @Nullable
    @Min(1)
    private Long parentId;

    @NotBlank
    @Size(max = 20)
    private String name;
}
