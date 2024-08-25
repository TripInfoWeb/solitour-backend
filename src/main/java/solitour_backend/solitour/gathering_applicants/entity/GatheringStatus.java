package solitour_backend.solitour.gathering_applicants.entity;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum GatheringStatus {
    WAIT("wait"),
    CONSENT("consent"),
    REFUSE("refuse");

    private final String name;

    GatheringStatus(String name) {
        this.name = name;
    }

    public static GatheringStatus fromName(String name) {
        return Arrays.stream(GatheringStatus.values())
                .filter(e -> e.name.equals(name))
                .findAny()
                .orElse(null);
    }
}
