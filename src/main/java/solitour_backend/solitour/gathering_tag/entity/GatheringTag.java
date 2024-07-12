package solitour_backend.solitour.gathering_tag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.tag.entity.Tag;

@Entity
@Getter
@Table(name = "gathering_tag")
@NoArgsConstructor
public class GatheringTag {

  @Id
  @Column(name = "gathering_tag_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "tag_id")
  private Tag tag;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "gathering_id")
  private Gathering gathering;

  public GatheringTag(Tag tag, Gathering gathering) {
    this.tag = tag;
    this.gathering = gathering;
  }
}
