package solitour_backend.solitour.user_image.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.user_image.dto.UserImageRequest;
import solitour_backend.solitour.user_image.dto.UserImageResponse;
import solitour_backend.solitour.user_image.service.UserImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-image")
public class UserImageController {

    private final UserImageService userImageService;


    @PostMapping
    public ResponseEntity<UserImageResponse> createUserImage(@RequestPart("request") UserImageRequest imageRequest,
                                                             @RequestPart("userImage") MultipartFile userImage,
                                                             BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);
        UserImageResponse informationResponse = userImageService.updateUserProfile(
                imageRequest.getUserId(), userImage);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(informationResponse);
    }

}
