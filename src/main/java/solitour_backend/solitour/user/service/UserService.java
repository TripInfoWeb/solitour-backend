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
import solitour_backend.solitour.gathering.dto.response.GatheringMypageResponse;
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
    private final S3Uploader s3Uploader;
    @Value("${user.profile.url.female}")
    private String femaleProfileUrl;
    @Value("${user.profile.male}")
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

    public Page<InformationBriefResponse> retrieveInformationOwner(Pageable pageable, Long userId) {
        return userRepository.retrieveInformationOwner(pageable, userId);
    }

    public Page<InformationBriefResponse> retrieveInformationBookmark(Pageable pageable, Long userId) {
        return userRepository.retrieveInformationBookmark(pageable, userId);
    }

    @Transactional
    public void updateUserProfile(Long userId, MultipartFile userProfile) {
        UserImageResponse response = userImageService.updateUserProfile(userId, userProfile);
        User user = userRepository.findByUserId(userId);
        checkUserProfile(user.getUserImage().getAddress());
        user.updateUserImage(response.getImageUrl());
    }

    @Transactional
    public void deleteUserProfile(Long userId) {
        User user = userRepository.findByUserId(userId);
        resetUserProfile(user,user.getUserImage().getAddress(),user.getSex());
    }

    private void resetUserProfile(User user, String imageUrl,String sex) {
       checkUserProfile(imageUrl);
       if(sex.equals("male")){
           user.updateUserImage(maleProfileUrl);
       } else if (sex.equals("female")) {
           user.updateUserImage(femaleProfileUrl);
       }else{
           user.updateUserImage(noneProfileUrl);
       }
    }

    private void checkUserProfile(String imageUrl) {
        if(imageUrl.equals(femaleProfileUrl) || imageUrl.equals(maleProfileUrl )|| imageUrl.equals(noneProfileUrl)){ {
            return;
        }
        s3Uploader.deleteImage(imageUrl);
    }

    public Page<GatheringMypageResponse> retrieveGatheringHost(Pageable pageable, Long userId) {
        return userRepository.retrieveGatheringHost(pageable, userId);
    }

    public Page<GatheringMypageResponse> retrieveGatheringBookmark(Pageable pageable, Long userId) {
        return userRepository.retrieveGatheringBookmark(pageable, userId);
    }

    public Page<GatheringApplicantResponse> retrieveGatheringApplicant(Pageable pageable, Long userId) {
        return userRepository.retrieveGatheringApplicant(pageable, userId);
    }
}
