package solitour_backend.solitour.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import solitour_backend.solitour.admin.entity.QnAMessage;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class QnaResponseDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private String status;
    private LocalDateTime updatedAt;
    private String categoryName;
    private Long userId;
    private String userNickname;
    private List<QnAMessage> qnaMessages;
}
