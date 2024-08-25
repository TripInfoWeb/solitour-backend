package solitour_backend.solitour.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TagRegisterRequest {

    @NotBlank
    @Size(min = 1, max = 40)
    private String name;
}
