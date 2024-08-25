package solitour_backend.solitour.admin.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import solitour_backend.solitour.admin.entity.Banner;
import solitour_backend.solitour.admin.repository.BannerRepository;
import solitour_backend.solitour.util.TimeUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerService {

    private final AmazonS3Client amazonS3Client;
    private final BannerRepository bannerRepository;
    private final TimeUtil timeUtil;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ResponseEntity getBannerList() {
        List<Banner> banners = bannerRepository.findAllByOrderById();
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("max-age=" + timeUtil.getSecondsUntilMidnightInKST());
        return new ResponseEntity<>(banners, HttpStatus.OK);
    }

    public ResponseEntity deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Banner not found"));

        try {
            amazonS3Client.deleteObject(bucket, banner.getName());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete image from S3");
        }

        bannerRepository.deleteById(id);
        // Step 4: Return a response
        return new ResponseEntity<>("Banner deleted successfully", HttpStatus.OK);
    }

    public Banner createBanner(MultipartFile multipartFile, String dirName) {
        String fileName = dirName + "/" + new Date().toString();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata));

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }
        String imageUrl = amazonS3Client.getUrl(bucket, fileName).toString();
        Banner banner = bannerRepository.save(Banner.builder().name(fileName).url(imageUrl).build());
        return banner;
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException se) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일 입니다.");
        }
    }

}
