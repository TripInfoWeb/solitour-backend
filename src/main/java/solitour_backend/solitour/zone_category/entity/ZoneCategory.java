package solitour_backend.solitour.zone_category.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "zone_category")
@NoArgsConstructor
@AllArgsConstructor
public class ZoneCategory {

  @Id
  @Column(name = "zone_category_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_zone_category_id")
  private ZoneCategory parentZoneCategory;

  @Column(name = "zone_category_name")
  private String name;

  public ZoneCategory(ZoneCategory parentZoneCategory, String name) {
    this.parentZoneCategory = parentZoneCategory;
    this.name = name;
  }
}
