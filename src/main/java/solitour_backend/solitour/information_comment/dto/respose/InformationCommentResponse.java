package solitour_backend.solitour.information_comment.dto.respose;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InformationCommentResponse {
    @NotBlank
    private Long commentId;
}
