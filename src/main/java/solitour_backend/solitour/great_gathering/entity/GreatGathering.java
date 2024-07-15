package solitour_backend.solitour.great_gathering.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.user.entity.User;

@Entity
@Getter
@Table(name = "great_gathering")
@NoArgsConstructor
public class GreatGathering {

  @Id
  @Column(name = "great_gathering_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gathering_id")
  private Gathering gathering;

}
