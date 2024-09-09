package solitour_backend.solitour.diary.dto.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DiaryUpdateRequest {
    private String title;
    private String deleteTitleImage;
    private String saveTitleImage;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private List<DiaryUpdateDayRequest> diaryDayRequests;

    @Getter
    @AllArgsConstructor
    public static class DiaryUpdateDayRequest {
        private String content;
        private String feelingStatus;
        private String deleteImagesUrl;
        private String saveImagesUrl;
        private String place;

        public List<String> getSplitImageUrl(String urlList) {
            if (urlList == null || urlList.isEmpty()) {
                return new ArrayList<>();
            }
            return Arrays.asList(urlList.split(","));
        }
    }


}
