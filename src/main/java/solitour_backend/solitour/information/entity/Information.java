package solitour_backend.solitour.information.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

@Entity
@Getter
@Setter
@Table(name = "information")
@NoArgsConstructor
public class Information {

    @Id
    @Column(name = "information_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_category_id")
    private ZoneCategory zoneCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "place_id")
    private Place place;


    @Column(name = "information_title")
    private String title;

    @Column(name = "information_address")
    private String address;

    @Column(name = "information_created_date")
    private LocalDateTime createdDate;

    @Column(name = "information_view_count")
    private Integer viewCount;

    @Column(name = "information_content")
    private String content;

    @Column(name = "information_tip")
    private String tip;

    public Information(Category category, ZoneCategory zoneCategory, User user, Place place,
                       String title, String address, LocalDateTime createdDate, Integer viewCount, String content,
                       String tip) {
        this.category = category;
        this.zoneCategory = zoneCategory;
        this.user = user;
        this.place = place;
        this.title = title;
        this.address = address;
        this.createdDate = createdDate;
        this.viewCount = viewCount;
        this.content = content;
        this.tip = tip;
    }
}
