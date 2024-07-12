package solitour_backend.solitour.book_mark_information.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.user.entity.User;

@Entity
@Getter
@Table(name = "book_mark_information")
@NoArgsConstructor
public class BookMarkInformation {

  @Id
  @Column(name = "book_mark_information_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "information_id")
  private Information information;

}
