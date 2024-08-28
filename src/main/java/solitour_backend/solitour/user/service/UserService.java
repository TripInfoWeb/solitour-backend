package solitour_backend.solitour.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.NicknameAlreadyExistsException;
import solitour_backend.solitour.user.repository.UserRepository;
import solitour_backend.solitour.user.service.dto.response.UserInfoResponse;
import solitour_backend.solitour.user_image.dto.UserImageResponse;
import solitour_backend.solitour.user_image.entity.UserImageRepository;
import solitour_backend.solitour.user_image.service.UserImageService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserImageService userImageService;
    private final UserImageRepository userImageRepository;

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

    public Page<InformationBriefResponse> retrieveUserInformationPostByUserId(Pageable pageable, Long userId) {
        return userRepository.getInformationByUserId(pageable, userId);
    }

    public Page<InformationBriefResponse> retrieveUserInformationPostByUserBookMark(Pageable pageable, Long userId) {
        return userRepository.getInformationByUserBookMark(pageable, userId);
    }

    @Transactional
    public void updateUserProfile(Long userId, MultipartFile userProfile) {
        UserImageResponse response = userImageService.registerInformation(userId, userProfile);
        User user = userRepository.findByUserId(userId);
        user.updateUserImage(response.getImageUrl());
    }
}
