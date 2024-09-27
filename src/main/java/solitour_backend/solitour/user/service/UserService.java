package solitour_backend.solitour.user.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.auth.entity.Term;
import solitour_backend.solitour.auth.entity.TermRepository;
import solitour_backend.solitour.gathering.dto.response.GatheringApplicantResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringMypageResponse;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.user.dto.request.AgreeUserInfoRequest;
import solitour_backend.solitour.user.dto.request.DisagreeUserInfoRequest;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.NicknameAlreadyExistsException;
import solitour_backend.solitour.user.repository.UserRepository;
import solitour_backend.solitour.user.service.dto.response.UserInfoResponse;
import solitour_backend.solitour.user_image.dto.UserImageResponse;
import solitour_backend.solitour.user_image.service.UserImageService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserImageService userImageService;
    private final TermRepository termRepository;
    private final S3Uploader s3Uploader;
    @Value("${user.profile.url.female}")
    private String femaleProfileUrl;
    @Value("${user.profile.url.male}")
    private String maleProfileUrl;
    @Value("${user.profile.url.none}")
    private String noneProfileUrl;

    public UserInfoResponse retrieveUserInfo(Long userId) {
        User user = userRepository.findByUserId(userId);

        return new UserInfoResponse(user);
    }

    @Transactional
    public void updateNickname(Long userId, String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new NicknameAlreadyExistsException("이미 존재하는 닉네임입니다.");
        }
        User user = userRepository.findByUserId(userId);
        user.updateNickname(nickname);
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
        resetUserProfile(user, user.getUserImage().getAddress(), user.getSex());
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

    @Transactional
    public void agreeUserInfo(Long userId, AgreeUserInfoRequest request) {
        User user = userRepository.findByUserId(userId);
        changeUserProfile(user, request);
        if(!termRepository.findByUser(user).isPresent()){
            saveTerm(user, request.getTermConditionAgreement(),request.getPrivacyPolicyAgreement());
        }
        user.agreeUserInfo(request);
    }

    @Transactional
    public void disagreeUserInfo(Long userId, DisagreeUserInfoRequest request) {
        User user = userRepository.findByUserId(userId);
        saveTerm(user, request.getTermConditionAgreement(),request.getPrivacyPolicyAgreement());
        user.disagreeUserInfo(request);
    }

    private void saveTerm(User user, Boolean termCondition, Boolean termPrivacy) {
        Term term = Term.builder()
                .user(user)
                .termCondition(termCondition)
                .termPrivacy(termPrivacy)
                .createdAt(LocalDateTime.now())
                .build();
        termRepository.save(term);
    }

    private void changeUserProfile(User user, AgreeUserInfoRequest request) {
        String sex = request.getSex();

        if(user.getUserImage().equals(noneProfileUrl) && sex.equals("male")){
            user.updateUserImage(maleProfileUrl);
        } else if (user.getUserImage().equals(noneProfileUrl) && sex.equals("female")) {
            user.updateUserImage(femaleProfileUrl);
        }
    }

    private void resetUserProfile(User user, String imageUrl, String sex) {
        checkUserProfile(imageUrl);
        if (sex.equals("male")) {
            user.updateUserImage(maleProfileUrl);
        } else if (sex.equals("female")) {
            user.updateUserImage(femaleProfileUrl);
        } else {
            user.updateUserImage(noneProfileUrl);
        }
    }

    private void checkUserProfile(String imageUrl) {
        if (imageUrl.equals(femaleProfileUrl) || imageUrl.equals(maleProfileUrl) || imageUrl.equals(noneProfileUrl)) {
            return;
        }
        s3Uploader.deleteImage(imageUrl);
    }
}
