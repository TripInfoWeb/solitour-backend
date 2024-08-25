package solitour_backend.solitour.util;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

@Component
public class TimeUtil {

    public long getSecondsUntilMidnightInKST() {
        ZoneId koreaZoneId = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(koreaZoneId);
        ZonedDateTime midnight = now.toLocalDate().atTime(LocalTime.MIDNIGHT)
                .plusDays(1)
                .atZone(koreaZoneId);
        Duration duration = Duration.between(now, midnight);
        return duration.getSeconds();
    }

}
