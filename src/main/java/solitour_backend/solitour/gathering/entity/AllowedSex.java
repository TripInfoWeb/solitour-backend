package solitour_backend.solitour.gathering.entity;

import java.util.Arrays;

import lombok.Getter;

@Getter
public enum AllowedSex {
    MALE("male"),
    FEMALE("female"),
    ALL("all");

    private final String name;

    AllowedSex(String name) {
        this.name = name;
    }

    public static AllowedSex fromName(String name) {
        return Arrays.stream(AllowedSex.values())
                .filter(e -> e.name.equals(name))
                .findAny()
                .orElseThrow(RuntimeException::new);
    }
}
