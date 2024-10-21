package solitour_backend.solitour.information_comment.dto.respose;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import solitour_backend.solitour.information_comment.entity.InformationComment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
