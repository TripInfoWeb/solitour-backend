package solitour_backend.solitour.user.user_status;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UserStatus {
    ACTIVATE("활성화"),
    DORMANT("휴먼"),
    DELETE("삭제"),
    MANAGER("관리자");

    private final String name;

    UserStatus(String name) {
        this.name = name;
    }

    public static UserStatus fromName(String name) {
        return Arrays.stream(UserStatus.values())
                .filter(e -> e.name.equals(name))
                .findAny()
                .orElse(null);
    }
}