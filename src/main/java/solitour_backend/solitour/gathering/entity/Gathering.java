package solitour_backend.solitour.gathering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "gathering")
@NoArgsConstructor
public class Gathering {

  @Id
  @Column(name = "gathering_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "zone_category_id")
  private ZoneCategory zoneCategory;

  @Column(name = "gathering_title")
  private String title;

  @Column(name = "gathering_person_count")
  private Integer personCount;

  @Column(name = "gathering_view_count")
  private Integer viewCount;

  @Column(name = "gathering_recent_date")
  private LocalDateTime recentDate;

  @Column(name = "gathering_is_edited")
  private Boolean isEdited;

  @Column(name = "gathering_content")
  private String content;
}
