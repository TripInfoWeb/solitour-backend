package solitour_backend.solitour.image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.image.dto.response.ImageResponse;
import solitour_backend.solitour.image.service.ImageService;
import solitour_backend.solitour.user_image.dto.UserImageRequest;
import solitour_backend.solitour.user_image.dto.UserImageResponse;
import solitour_backend.solitour.user_image.service.UserImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/image")
public class ImageController {

    private final ImageService imageService;


    @Authenticated
    @PostMapping
    public ResponseEntity<ImageResponse> uploadImage(@RequestParam Long id,
                                                     @RequestPart("image") MultipartFile userImage,
                                                     @RequestParam String type,
                                                     @RequestParam String imageStatus) {
        ImageResponse imageResponse = imageService.uploadImage(id, userImage, type,
                imageStatus);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(imageResponse);
    }

}
