package solitour_backend.solitour.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class QnARegisterRequest {

    @NotNull
    @Min(1)
    private String title;

    @NotNull
    @Min(1)
    private String content;

    @NotNull
    private String categoryName;
}
