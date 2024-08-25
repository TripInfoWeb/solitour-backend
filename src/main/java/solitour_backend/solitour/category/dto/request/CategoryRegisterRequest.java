package solitour_backend.solitour.category.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryRegisterRequest {

    @Nullable
    private Long parentCategory;

    @NotBlank
    @Size(min = 2, max = 20)
    private String name;
}
