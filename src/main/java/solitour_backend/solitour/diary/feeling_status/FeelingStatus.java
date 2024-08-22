package solitour_backend.solitour.diary.feeling_status;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum FeelingStatus {
    EXCITED("최고"),
    NICE("좋아"),
    SOSO("무난"),
    SAD("슬퍼"),
    ANGRY("화나");

    private final String status;

    FeelingStatus(String status) {
        this.status = status;
    }

    public static FeelingStatus fromName(String status) {
        return Arrays.stream(FeelingStatus.values())
                .filter(e -> e.getStatus().equals(status))
                .findAny()
                .orElse(null);
    }
}
