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
import solitour_backend.solitour.user.dto.request.UpdateUserInfoRequest;
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
        UserImageResponse response = userImageService.registerInformation(userId, userProfile);
        User user = userRepository.findByUserId(userId);
        user.updateUserImage(response.getImageUrl());
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
    public void updateUserInfo(Long userId, UpdateUserInfoRequest request) {
        User user = userRepository.findByUserId(userId);
        user.updateUserInfo(request);
    }
}
