package solitour_backend.solitour.information_comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class InformationCommentRequest {
    @NotBlank
    private String comment;
}
