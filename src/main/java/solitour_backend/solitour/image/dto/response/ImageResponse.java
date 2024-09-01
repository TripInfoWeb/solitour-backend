package solitour_backend.solitour.image.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageResponse {
    private String imageStatus;
    private String address;
}