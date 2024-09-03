package solitour_backend.solitour.image.image_status;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ImageType {
    USER("user"),
    DIARY("diary"),
    GATHERING("gathering"),
    INFORMATION("information");

    private final String name;

    ImageType(String name) {
        this.name = name;
    }

    public static ImageType fromName(String name) {
        return Arrays.stream(ImageType.values())
                .filter(e -> e.name.equals(name))
                .findAny()
                .orElse(null);
    }
}