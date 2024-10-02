package solitour_backend.solitour.user_image.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.user_image.dto.UserImageResponse;
import solitour_backend.solitour.user_image.entity.UserImage;
import solitour_backend.solitour.user_image.entity.UserImageRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserImageService {

    private final UserImageRepository userImageRepository;
    private final S3Uploader s3Uploader;
    public static final String IMAGE_PATH = "user";

    @Transactional
    public UserImage saveUserImage(String imageUrl) {
        UserImage userImage = new UserImage(imageUrl, LocalDate.now());

        userImageRepository.save(userImage);

        return userImage;
    }

    @Transactional
    public UserImageResponse updateUserProfile(Long userId, MultipartFile userImage) {

        String userImageUrl = s3Uploader.upload(userImage, IMAGE_PATH, userId);
        s3Uploader.markImagePermanent(userImageUrl);

        return new UserImageResponse(userImageUrl);
    }
}
