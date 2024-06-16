package solitour_backend.solitour.information.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

import java.time.LocalDateTime;

@Entity
@Getter
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

    @ManyToOne(fetch = FetchType.LAZY)
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

}
