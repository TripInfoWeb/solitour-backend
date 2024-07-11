package solitour_backend.solitour.user_image.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.user_image.entity.UserImage;
import solitour_backend.solitour.user_image.entity.UserImageRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserImageService {

  private final UserImageRepository userImageRepository;

  @Transactional
  public UserImage saveUserImage(String imageUrl) {
    UserImage userImage = new UserImage(imageUrl, LocalDate.now());

    userImageRepository.save(userImage);

    return userImage;
  }

}
