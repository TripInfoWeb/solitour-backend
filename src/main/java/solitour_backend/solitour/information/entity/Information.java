package solitour_backend.solitour.information.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

@Entity
@Getter
@Setter
@Table(name = "information")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Column(name = "information_created_date")
    private LocalDateTime createdDate;

    @Column(name = "information_view_count")
    private Integer viewCount;

    @Column(name = "information_content")
    private String content;

    @Column(name = "information_tip")
    private String tip;

    public Information(Category category, ZoneCategory zoneCategory, User user, Place place,
                       String title, String address, Integer viewCount, String content,
                       String tip) {
        this.category = category;
        this.zoneCategory = zoneCategory;
        this.user = user;
        this.place = place;
        this.title = title;
        this.address = address;
        this.viewCount = viewCount;
        this.content = content;
        this.tip = tip;
    }

    public void upViewCount() {
        this.viewCount++;
    }
}
