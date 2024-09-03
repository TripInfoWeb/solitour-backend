package solitour_backend.solitour.admin.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "qna")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class QnA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id")
    private Long id;

    @Column(name = "qna_title")
    private String title;

    @CreatedDate
    @Column(name = "qna_created_at")
    private LocalDateTime createdAt;

    @Column(name = "qna_status")
    private String status;

    @LastModifiedDate
    @Column(name = "qna_updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "qna_category_name")
    private String categoryName;

    @Column(name = "user_id")
    private Long userId;

    @Builder.Default
    @OneToMany(mappedBy = "qna", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<QnAMessage> qnaMessages = new ArrayList<>();
}
