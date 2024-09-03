package solitour_backend.solitour.admin.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "qna_message")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QnAMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "qna_id")
    @JsonBackReference
    private QnA qna;

    @CreatedDate
    @Column(name = "qna_message_created_at")
    private LocalDateTime createdAt;

    @Column(name = "qna_message_user_id")
    private Long userId;

    @Column(name = "qna_message_content")
    private String content;
}
