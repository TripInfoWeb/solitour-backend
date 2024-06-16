package solitour_backend.solitour.zone_category.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "zone_category")
@NoArgsConstructor
public class ZoneCategory {

    @Id
    @Column(name = "zone_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(name = "zone_category_name")
    private String categoryName;

    public ZoneCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
