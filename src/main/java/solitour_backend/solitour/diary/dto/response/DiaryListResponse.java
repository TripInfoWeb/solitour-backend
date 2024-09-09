package solitour_backend.solitour.diary.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import solitour_backend.solitour.diary.diary_day_content.DiaryDayContent;
import solitour_backend.solitour.diary.entity.Diary;

@Getter
public class DiaryListResponse {
    private final List<DiaryContent> diaryContentResponse;

    public DiaryListResponse(List<List<Diary>> diaries) {

        diaryContentResponse = diaries.stream().flatMap(List::stream).map(
                diary -> DiaryContent.builder()
                        .diaryId(diary.getId())
                        .title(diary.getTitle())
                        .titleImage(diary.getTitleImage())
                        .startDatetime(diary.getStartDatetime())
                        .endDatetime(diary.getEndDatetime())
                        .diaryDayContentResponses(new DiaryDayContentResponse(diary.getDiaryDayContent())).build()
        ).collect(Collectors.toList());

    }

    @Getter
    @Builder
    private static class DiaryContent {
        private Long diaryId;
        private String title;
        private String titleImage;
        private LocalDateTime startDatetime;
        private LocalDateTime endDatetime;
        private DiaryDayContentResponse diaryDayContentResponses;
    }

    @Getter
    private static class DiaryDayContentResponse {

        private final List<DiaryDayContentDetail> diaryDayContentDetail;

        private DiaryDayContentResponse(List<DiaryDayContent> diaryDayContent) {
            this.diaryDayContentDetail = diaryDayContent.stream()
                    .map(diaryDayContentDetail ->
                            DiaryDayContentDetail.builder()
                                    .content(diaryDayContentDetail.getContent())
                                    .feelingStatus(diaryDayContentDetail.getFeelingStatus().name())
                                    .place(diaryDayContentDetail.getPlace())
                                    .build()
                    ).collect(Collectors.toList());
        }

    }

    @Getter
    @Builder
    private static class DiaryDayContentDetail {
        private String content;
        private String feelingStatus;
        private String place;
    }

}
