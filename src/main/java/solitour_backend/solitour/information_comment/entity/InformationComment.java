package solitour_backend.solitour.information_comment.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information_comment.dto.request.InformationCommentRequest;
import solitour_backend.solitour.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "information_comment")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class InformationComment {

    @Id
    @Column(name = "information_comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "information_id")
    private Information information;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "information_comment_content")
    private String content;

    @CreatedDate
    @Column(name = "information_comment_created_date")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "information_comment_updated_date")
    private LocalDateTime updatedDate;

    public void updateComment(@Valid InformationCommentRequest informationCommentRequest) {
        this.content = informationCommentRequest.getComment();
    }
}
