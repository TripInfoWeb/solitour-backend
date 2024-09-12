package solitour_backend.solitour.diary.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DiaryDayContentDetail {
    private String content;
    private String feelingStatus;
    private String place;
    private String contentImage;
}
