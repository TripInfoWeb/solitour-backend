package solitour_backend.solitour.place.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "place")
@NoArgsConstructor
public class Place {

    @Id
    @Column(name = "place_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_search_id")
    private String searchId;

    @Column(name = "place_name")
    private String name;

    @Column(name = "place_x_axis")
    private BigDecimal xAxis;

    @Column(name = "place_y_axis")
    private BigDecimal yAxis;

    @Column(name = "place_address")
    private String address;

    @Column(name = "place_is_custom")
    private Boolean isCustom;

    public Place(String searchId, String name, BigDecimal xAxis, BigDecimal yAxis, String address, Boolean isCustom) {
        this.searchId = searchId;
        this.name = name;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.address = address;
        this.isCustom = isCustom;
    }
}
