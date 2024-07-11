package solitour_backend.solitour.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.entity.UserRepository;
import solitour_backend.solitour.user.service.dto.response.UserInfoResponse;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

  private final UserRepository userRepository;

  public UserInfoResponse retrieveUserInfo(Long userId) {
    User user = userRepository.findByUserId(userId);

    return new UserInfoResponse(user);
  }

}
