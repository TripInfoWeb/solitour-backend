package solitour_backend.solitour.book_mark_gathering.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.user.entity.User;

@Entity
@Getter
@Table(name = "book_mark_gathering")
@NoArgsConstructor
public class BookMarkGathering {

  @Id
  @Column(name = "book_mark_gathering_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gathering_id")
  private Gathering gathering;
}
