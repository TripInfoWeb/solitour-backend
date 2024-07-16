package solitour_backend.solitour.category.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "category")
@NoArgsConstructor
public class Category {

  @Id
  @Column(name = "category_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "parent_category_id")
  private Category parentCategory;

  @Column(name = "category_name")
  private String name;

  public Category(Category parentCategory, String name) {
    this.parentCategory = parentCategory;
    this.name = name;
  }
}
