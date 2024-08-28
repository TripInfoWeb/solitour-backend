package solitour_backend.solitour.image.image_status;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ImageStatus {
    THUMBNAIL("썸네일"),
    CONTENT("본문"),
    USER("회원"),
    NONE("없음");

    private final String name;

    ImageStatus(String name) {
        this.name = name;
    }

    public static ImageStatus fromName(String name) {
        return Arrays.stream(ImageStatus.values())
                .filter(e -> e.name.equals(name))
                .findAny()
                .orElse(null);
    }
}