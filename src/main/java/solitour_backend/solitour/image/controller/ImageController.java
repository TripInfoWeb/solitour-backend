package solitour_backend.solitour.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.image.dto.response.S3FileResponse;
import solitour_backend.solitour.image.image_status.ImageType;
import solitour_backend.solitour.image.service.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;
    @Value("${image.env}")
    private String env;


    @Authenticated
    @PostMapping
    public ResponseEntity<S3FileResponse> uploadImage(@AuthenticationPrincipal Long userId,
                                                      @RequestPart("image") MultipartFile userImage,
                                                      @RequestParam ImageType type) {
        checkType(type);
        String path = env.concat("/").concat(type.getName());
        S3FileResponse s3FileResponse = imageService.uploadImage(userId, userImage, path);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(s3FileResponse);
    }

    private void checkType(ImageType type) {
        switch (type) {
            case USER:
            case DIARY:
            case GATHERING:
            case INFORMATION:
                break;
            default:
                throw new IllegalArgumentException("잘못된 타입입니다.");
        }
    }

}
