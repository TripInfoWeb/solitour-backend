package solitour_backend.solitour.information_comment.dto.respose;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class InformationCommentListResponse {
    private Long commentId;
    private Long userId;
    private String userNickname;
    private String userProfile;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
