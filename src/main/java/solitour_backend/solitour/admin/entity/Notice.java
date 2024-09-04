package solitour_backend.solitour.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notice")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Notice {
    @Id
    @Column(name = "notice_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notice_title")
    private String title;

    @Column(name = "notice_content")
    private String content;

    @CreatedDate
    @Column(name = "notice_created_at")
    private LocalDateTime createdAt;

    @Column(name = "notice_category_name")
    private String categoryName;

    @Builder.Default
    @Column(name = "notice_is_deleted")
    private Boolean isDeleted = false;
}
