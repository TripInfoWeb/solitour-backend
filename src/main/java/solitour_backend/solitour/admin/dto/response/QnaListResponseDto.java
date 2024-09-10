package solitour_backend.solitour.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class QnaListResponseDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private String status;
    private LocalDateTime updatedAt;
    private String categoryName;
    private Long userId;
    private String userNickname;
}
