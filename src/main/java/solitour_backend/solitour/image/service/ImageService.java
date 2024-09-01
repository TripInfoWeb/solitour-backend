package solitour_backend.solitour.image.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.image.dto.response.S3FileResponse;
import solitour_backend.solitour.image.s3.S3Uploader;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {

    private final S3Uploader s3Uploader;

    public S3FileResponse uploadImage(Long id, MultipartFile image, String path) {
        String imageUrl = s3Uploader.upload(image, path, id);

        return new S3FileResponse(imageUrl);
    }
}
