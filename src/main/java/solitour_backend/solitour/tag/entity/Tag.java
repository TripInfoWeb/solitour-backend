package solitour_backend.solitour.tag.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Table(name = "tag")
@NoArgsConstructor
@Setter
public class Tag {

  @Id
  @Column(name = "tag_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long tagId;

  @Column(name = "tag_name")
  private String name;

  public Tag(String name) {
    this.name = name;
  }
}
