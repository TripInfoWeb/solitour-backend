package solitour_backend.solitour.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
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
