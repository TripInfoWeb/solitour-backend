package solitour_backend.solitour.gathering.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import solitour_backend.solitour.gathering_category.entity.GatheringCategory;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

@Entity
@Getter
@Setter
@Table(name = "gathering")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Gathering {

    @Id
    @Column(name = "gathering_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_category_id")
    private ZoneCategory zoneCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gathering_category_id")
    private GatheringCategory gatheringCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(name = "gathering_title")
    private String title;

    @Column(name = "gathering_content")
    private String content;

    @Column(name = "gathering_person_count")
    private Integer personCount;

    @Column(name = "gathering_view_count")
    private Integer viewCount;

    @CreatedDate
    @Column(name = "gathering_created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "gathering_edited_at")
    private LocalDateTime editedAt;

    @Column(name = "gathering_schedule_start_date")
    private LocalDateTime scheduleStartDate;

    @Column(name = "gathering_schedule_end_date")
    private LocalDateTime scheduleEndDate;

    @Column(name = "gathering_is_finish")
    private Boolean isFinish;

    @Column(name = "gathering_deadline")
    private LocalDateTime deadline;

    @Column(name = "gathering_allowed_sex")
    @Convert(converter = AllowedSexConverter.class)
    private AllowedSex allowedSex;

    @Column(name = "gathering_start_age")
    private Integer startAge;

    @Column(name = "gathering_end_age")
    private Integer endAge;

    @Column(name = "gathering_is_deleted")
    private Boolean isDeleted;

    @Column(name = "gathering_open_chatting_url")
    private String openChattingUrl;

    public Gathering(User user, ZoneCategory zoneCategory, GatheringCategory gatheringCategory, Place place,
                     String title, String content, Integer personCount, Integer viewCount,
                     LocalDateTime scheduleStartDate, LocalDateTime scheduleEndDate, Boolean isFinish,
                     LocalDateTime deadline, AllowedSex allowedSex, Integer startAge, Integer endAge, String openChattingUrl) {
        this.user = user;
        this.zoneCategory = zoneCategory;
        this.gatheringCategory = gatheringCategory;
        this.place = place;
        this.title = title;
        this.content = content;
        this.personCount = personCount;
        this.viewCount = viewCount;
        this.scheduleStartDate = scheduleStartDate;
        this.scheduleEndDate = scheduleEndDate;
        this.isFinish = isFinish;
        this.deadline = deadline;
        this.allowedSex = allowedSex;
        this.startAge = startAge;
        this.endAge = endAge;
        this.isDeleted = false;
        this.openChattingUrl = openChattingUrl;
    }

    public void upViewCount() {
        this.viewCount++;
    }
}
