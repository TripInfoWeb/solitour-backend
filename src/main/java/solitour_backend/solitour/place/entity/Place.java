package solitour_backend.solitour.place.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "place")
@NoArgsConstructor
@AllArgsConstructor
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
    private BigDecimal xaxis;

    @Column(name = "place_y_axis")
    private BigDecimal yaxis;

    @Column(name = "place_address")
    private String address;

    public Place(String searchId, String name, BigDecimal xaxis, BigDecimal yaxis, String address) {
        this.searchId = searchId;
        this.name = name;
        this.xaxis = xaxis;
        this.yaxis = yaxis;
        this.address = address;
    }
}