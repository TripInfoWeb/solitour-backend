package solitour_backend.solitour.user_image.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.image.entity.Image;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.info_tag.entity.InfoTag;
import solitour_backend.solitour.information.dto.request.InformationRegisterRequest;
import solitour_backend.solitour.information.dto.response.InformationResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.tag.entity.Tag;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.user_image.dto.UserImageResponse;
import solitour_backend.solitour.user_image.entity.UserImage;
import solitour_backend.solitour.user_image.entity.UserImageRepository;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryNotExistsException;

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
  public UserImageResponse registerInformation(Long userId, MultipartFile userImage) {

    String userImageUrl = s3Uploader.upload(userImage, IMAGE_PATH, userId);

    return new UserImageResponse(userImageUrl);
  }

}
