package solitour_backend.solitour.image.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.image.dto.response.ImageResponse;
import solitour_backend.solitour.image.entity.Image;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.image.repository.ImageRepository;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.user_image.entity.UserImageRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ImageService {

    private final UserImageRepository userImageRepository;
    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public ImageResponse uploadImage(Long id, MultipartFile image, String type, String imageStatus) {
        String imageUrl = s3Uploader.upload(image, type, id);
        ImageStatus status = checkImageStatus(imageStatus);
        Image contentImage = new Image(status, imageUrl, LocalDate.now());
        imageRepository.save(contentImage);
        return new ImageResponse(contentImage.getImageStatus().getName(), contentImage.getAddress());
    }

    private ImageStatus checkImageStatus(String imageStatus) {
        switch (imageStatus) {
            case "CONTENT" -> {
                return ImageStatus.CONTENT;
            }
            case "USER" -> {
                return ImageStatus.USER;
            }
            case "THUMNAIl" -> {
                return ImageStatus.THUMBNAIL;
            }
        }
        return ImageStatus.NONE;
    }
}
