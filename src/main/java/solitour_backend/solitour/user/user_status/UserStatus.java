package solitour_backend.solitour.user.user_status;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum UserStatus {
    PENDING("대기"),
    ACTIVATE("활성화"),
    INACTIVATE("비활성화"),
    BLOCK("차단"),
    DORMANT("휴먼"),
    DELETE("삭제"),
    MANAGER("관리자");

    private final String name;

    UserStatus(String name) {
        this.name = name;
    }

    public static UserStatus fromName(String name) {
        return Arrays.stream(UserStatus.values())
                .filter(e -> e.getName().equals(name))
                .findAny()
                .orElse(null);
    }
}
