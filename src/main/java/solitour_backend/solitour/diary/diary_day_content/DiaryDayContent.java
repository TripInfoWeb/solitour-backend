package solitour_backend.solitour.diary.diary_day_content;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import solitour_backend.solitour.diary.entity.Diary;
import solitour_backend.solitour.diary.feeling_status.FeelingStatus;
import solitour_backend.solitour.diary.feeling_status.FeelingStatusConverter;

@Entity
@Getter
@Table(name = "diary_day_content")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DiaryDayContent {

    @Id
    @Column(name = "diary_day_content_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(name = "diary_day_content_place")
    private String place;

    @Column(name = "diary_day_content_image")
    private String contentImage;

    @Column(columnDefinition = "LONGTEXT", name = "diary_day_content_content")
    private String content;

    @Column(name = "diary_day_content_feeling_status")
    @Convert(converter = FeelingStatusConverter.class)
    private FeelingStatus feelingStatus;

    public List<String> getDiaryDayContentImagesList() {
        if (contentImage == null || contentImage.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(contentImage.split(","));
    }

    public void setDiaryDayContentImagesList(List<String> imageUrls) {
        this.contentImage = String.join(",", imageUrls);
    }
}
