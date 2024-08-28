package solitour_backend.solitour.gathering_category.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "gathering_category")
@NoArgsConstructor
public class GatheringCategory {

    @Id
    @Column(name = "gathering_category_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "gathering_category_name")
    private String name;

    public GatheringCategory(String name) {
        this.name = name;
    }
}
