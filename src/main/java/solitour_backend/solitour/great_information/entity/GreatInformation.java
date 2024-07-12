package solitour_backend.solitour.great_information.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.user.entity.User;

@Entity
@Getter
@Table(name = "great_information")
@NoArgsConstructor
public class GreatInformation {

  @Id
  @Column(name = "great_information_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "information_id")
  private Information information;
}
