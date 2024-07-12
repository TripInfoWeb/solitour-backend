package solitour_backend.solitour.comment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor
public class Comment {

  @Id
  @Column(name = "comment_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_comment_id")
  private Comment parentComment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "information_id")
  private Information information;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gathering_id")
  private Gathering gathering;

  @Column(name = "comment_content")
  private String content;

  @Column(name = "comment_recent_date")
  private LocalDateTime recentDate;

  @Column(name = "comment_is_edited")
  private Boolean isEdited;
}
