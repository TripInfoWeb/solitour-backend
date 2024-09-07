package solitour_backend.solitour.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.gathering.dto.response.GatheringApplicantResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.NicknameAlreadyExistsException;
import solitour_backend.solitour.user.repository.UserRepository;
import solitour_backend.solitour.user.service.dto.response.UserInfoResponse;
import solitour_backend.solitour.user_image.dto.UserImageResponse;
import solitour_backend.solitour.user_image.entity.UserImage;
import solitour_backend.solitour.user_image.entity.UserImageRepository;
import solitour_backend.solitour.user_image.service.UserImageService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserImageService userImageService;
    private final UserImageRepository userImageRepository;
    private final S3Uploader s3Uploader;
    @Value("${user.profile.url.female}")
    private String femaleProfileUrl;
    @Value("${user.profile.url.male}")
    private String maleProfileUrl;

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
        UserImage userImage = userImageRepository.findById(user.getUserImage().getId()).orElseThrow();
        changeToDefaultProfile(user, userImage);
        user.deleteUser();
    }

    public Page<InformationBriefResponse> retrieveInformationOwner(Pageable pageable, Long userId) {
        return userRepository.retrieveInformationOwner(pageable, userId);
    }

    public Page<InformationBriefResponse> retrieveInformationBookmark(Pageable pageable, Long userId) {
        return userRepository.retrieveInformationBookmark(pageable, userId);
    }

    @Transactional
    public void updateUserProfile(Long userId, MultipartFile userProfile) {
        UserImageResponse response = userImageService.registerInformation(userId, userProfile);
        User user = userRepository.findByUserId(userId);
        user.updateUserImage(response.getImageUrl());
    }

    public Page<GatheringBriefResponse> retrieveGatheringHost(Pageable pageable, Long userId) {
        return userRepository.retrieveGatheringHost(pageable, userId);
    }

    public Page<GatheringBriefResponse> retrieveGatheringBookmark(Pageable pageable, Long userId) {
        return userRepository.retrieveGatheringBookmark(pageable, userId);
    }

    public Page<GatheringApplicantResponse> retrieveGatheringApplicant(Pageable pageable, Long userId) {
        return userRepository.retrieveGatheringApplicant(pageable, userId);
    }

    private void changeToDefaultProfile(User user, UserImage userImage) {
        String defaultImageUrl = getDefaultProfile(user);
        deleteUserProfileFromS3(userImage,defaultImageUrl);
    }

    private String getDefaultProfile(User user) {
        String sex = user.getSex();
        if(sex.equals("male")){{
            return maleProfileUrl;
        }} else {
            return femaleProfileUrl;
        }
    }

    private void deleteUserProfileFromS3(UserImage userImage,String defaultImageUrl) {
        String userImageUrl = userImage.getAddress();
        if(userImageUrl.equals(femaleProfileUrl) || userImageUrl.equals(maleProfileUrl)){
            return;
        }
        s3Uploader.deleteImage(userImageUrl);
        userImage.changeToDefaultProfile(defaultImageUrl);
    }
}
