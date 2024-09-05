package solitour_backend.solitour.image.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    public String upload(MultipartFile multipartFile, String dirName, Long id) {

        String fileName = dirName + "/" + id + "/" + createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        List<Tag> tags = new ArrayList<>();
        tags.add(new Tag("temp", "true"));


        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucket, fileName, inputStream, metadata).withTagging(new ObjectTagging(tags));
            amazonS3Client.putObject(putObjectRequest);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void markImagePermanent(String imageUrl) {
        String fileName = extractFileNameFromUrl(imageUrl);
        GetObjectTaggingRequest getTaggingRequest = new GetObjectTaggingRequest(bucket, fileName);
        GetObjectTaggingResult getTaggingResult = amazonS3Client.getObjectTagging(getTaggingRequest);
        List<Tag> tags = getTaggingResult.getTagSet();

        boolean tagFound = false;

        for (Tag tag : tags) {
            if (tag.getKey().equals("temp")) {
                tag.setValue("false");
                tagFound = true;
                break;
            }
        }

        if (!tagFound) {
            tags.add(new Tag("temp", "false"));
        }

        SetObjectTaggingRequest setObjectTaggingRequest = new SetObjectTaggingRequest(bucket, fileName, new ObjectTagging(tags));
        amazonS3Client.setObjectTagging(setObjectTaggingRequest);
    }

    public void deleteImage(String fileUrl) {
        String fileName = extractFileNameFromUrl(fileUrl);

        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
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

    private String extractFileNameFromUrl(String url) {
        final String splitStr = ".com/";
        return url.substring(url.lastIndexOf(splitStr) + splitStr.length());
    }
}