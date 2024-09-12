package solitour_backend.solitour.diary.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import solitour_backend.solitour.diary.diary_day_content.DiaryDayContent;
import solitour_backend.solitour.diary.entity.Diary;

@Getter
@Builder
@AllArgsConstructor
public class DiaryContent {
    private Long diaryId;
    private String title;
    private String titleImage;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private DiaryDayContentResponse diaryDayContentResponses;

    public static DiaryContent from(Diary diary) {
        return DiaryContent.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .titleImage(diary.getTitleImage())
                .startDatetime(diary.getStartDatetime())
                .endDatetime(diary.getEndDatetime())
                .diaryDayContentResponses(new DiaryDayContentResponse(diary.getDiaryDayContent()))
                .build();
    }

    @Getter
    public static class DiaryDayContentResponse {
        private final List<DiaryDayContentDetail> diaryDayContentDetail;

        public DiaryDayContentResponse(List<DiaryDayContent> diaryDayContent) {
            this.diaryDayContentDetail = diaryDayContent.stream()
                    .map(diaryDayContentDetail ->
                            new DiaryDayContentDetail(
                                    diaryDayContentDetail.getContent(),
                                    diaryDayContentDetail.getFeelingStatus().name(),
                                    diaryDayContentDetail.getPlace(),
                                    diaryDayContentDetail.getContentImage()
                            )
                    ).collect(Collectors.toList());
        }
    }
}
