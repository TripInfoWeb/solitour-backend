package solitour_backend.solitour.diary.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryCreateRequest {
    private String title;
    private String titleImage;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private List<DiaryDayRequest> diaryDayRequests;

    @Getter
    @AllArgsConstructor
    public static class DiaryDayRequest {
        private String content;
        private String feelingStatus;
        private String diaryDayContentImages;
        private String place;
    }
}
