package solitour_backend.solitour.diary.entity;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import solitour_backend.solitour.diary.diary_day_content.DiaryDayContent;
import solitour_backend.solitour.diary.dto.DiaryRequest;
import solitour_backend.solitour.diary.dto.DiaryRequest.DiaryDayRequest;
import solitour_backend.solitour.diary.feeling_status.FeelingStatus;
import solitour_backend.solitour.diary.feeling_status.FeelingStatusConverter;
import solitour_backend.solitour.gathering.entity.AllowedSex;
import solitour_backend.solitour.gathering.entity.AllowedSexConverter;
import solitour_backend.solitour.gathering_category.entity.GatheringCategory;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.user_status.UserStatusConverter;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;


@Entity
@Getter
@Table(name = "diary")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Diary {

    @Id
    @Column(name = "diary_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "diary_title")
    private String title;

    @Column(name = "diary_start_date")
    private LocalDateTime startDatetime;

    @Column(name = "diary_end_date")
    private LocalDateTime endDatetime;

    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "diary")
    private List<DiaryDayContent> diaryDayContent;

    @CreatedDate
    @Column(name = "diary_created_date")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "diary_edited_date")
    private LocalDateTime editedAt;

    public void updateDiary(DiaryRequest request) {
        this.title = request.getTitle();
        this.startDatetime = request.getStartDatetime();
        this.endDatetime = request.getEndDatetime();
    }
}
