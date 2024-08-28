package solitour_backend.solitour.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.entity.UserRepository;
import solitour_backend.solitour.user.exception.NicknameAlreadyExistsException;
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

    @Transactional
    public void updateNickname(Long userId, String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new NicknameAlreadyExistsException("Nickname already exists");
        }
        User user = userRepository.findByUserId(userId);
        user.updateNickname(nickname);
    }

    @Transactional
    public void updateAgeAndSex(Long userId, String age, String sex) {
        User user = userRepository.findByUserId(userId);
        user.updateAgeAndSex(age, sex);
    }


    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        user.deleteUser(userId);
    }
    @Transactional
    public void updateUserProfile(Long userId, MultipartFile userProfile) {
        UserImageResponse response = userImageService.registerInformation(userId,userProfile);
        User user = userRepository.findByUserId(userId);
        user.updateUserImage(response.getImageUrl());
    }
}
