package solitour_backend.solitour.user_image.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserImageRequest {

    @NotBlank
    private Long userId;
}