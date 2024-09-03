package solitour_backend.solitour.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QuestionRegisterRequest {

    @NotNull
    @Min(1)
    private String content;

    @NotNull
    private Long qnaId;
}
