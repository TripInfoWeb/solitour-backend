package solitour_backend.solitour.auth.service;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.auth.entity.Token;
import solitour_backend.solitour.auth.entity.TokenRepository;
import solitour_backend.solitour.auth.exception.RevokeFailException;
import solitour_backend.solitour.auth.exception.UnsupportedLoginTypeException;
import solitour_backend.solitour.auth.service.dto.response.AccessTokenResponse;
import solitour_backend.solitour.auth.service.dto.response.LoginResponse;
import solitour_backend.solitour.auth.service.dto.response.OauthLinkResponse;
import solitour_backend.solitour.auth.support.JwtTokenProvider;
import solitour_backend.solitour.auth.support.RandomNickName;
import solitour_backend.solitour.auth.support.google.GoogleConnector;
import solitour_backend.solitour.auth.support.google.GoogleProvider;
import solitour_backend.solitour.auth.support.google.dto.GoogleUserResponse;
import solitour_backend.solitour.auth.support.kakao.KakaoConnector;
import solitour_backend.solitour.auth.support.kakao.KakaoProvider;
import solitour_backend.solitour.auth.support.kakao.dto.KakaoTokenAndUserResponse;
import solitour_backend.solitour.auth.support.kakao.dto.KakaoTokenResponse;
import solitour_backend.solitour.auth.support.kakao.dto.KakaoUserResponse;
import solitour_backend.solitour.auth.support.kakao.dto.request.CreateUserInfoRequest;
import solitour_backend.solitour.auth.support.naver.NaverConnector;
import solitour_backend.solitour.auth.support.naver.NaverProvider;
import solitour_backend.solitour.auth.support.naver.dto.NaverTokenAndUserResponse;
import solitour_backend.solitour.auth.support.naver.dto.NaverTokenResponse;
import solitour_backend.solitour.auth.support.naver.dto.NaverUserResponse;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.BlockedUserException;
import solitour_backend.solitour.user.exception.DeletedUserException;
import solitour_backend.solitour.user.exception.DormantUserException;
import solitour_backend.solitour.user.repository.UserRepository;
import solitour_backend.solitour.user.user_status.UserStatus;
import solitour_backend.solitour.user_image.entity.UserImage;
import solitour_backend.solitour.user_image.entity.UserImageRepository;
import solitour_backend.solitour.user_image.service.UserImageService;

@RequiredArgsConstructor
@Service
public class OauthService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoConnector kakaoConnector;
    private final KakaoProvider kakaoProvider;
    private final GoogleConnector googleConnector;
    private final GoogleProvider googleProvider;
    private final NaverConnector naverConnector;
    private final NaverProvider naverProvider;
    private final UserImageService userImageService;
    private final TokenRepository tokenRepository;
    private final UserImageRepository userImageRepository;
    private final S3Uploader s3Uploader;
    @Value("${user.profile.url.male}")
    private String USER_PROFILE_MALE;
    @Value("${user.profile.url.female}")
    private String USER_PROFILE_FEMALE;
    @Value("${user.profile.url.none}")
    private String USER_PROFILE_NONE;

    public OauthLinkResponse generateAuthUrl(String type, String redirectUrl) {
        String oauthLink = getAuthLink(type, redirectUrl);
        return new OauthLinkResponse(oauthLink);
    }

    @Transactional
    public LoginResponse requestAccessToken(String type, String code, String redirectUrl) {
        User user = checkAndSaveUser(type, code, redirectUrl);
        user.updateLoginTime();
        final int ACCESS_COOKIE_AGE = (int) TimeUnit.MINUTES.toSeconds(30);
        final int REFRESH_COOKIE_AGE = (int) TimeUnit.DAYS.toSeconds(30);

        String token = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        tokenService.synchronizeRefreshToken(user, refreshToken);

        Cookie accessCookie = createCookie("access_token", token, ACCESS_COOKIE_AGE);
        Cookie refreshCookie = createCookie("refresh_token", refreshToken, REFRESH_COOKIE_AGE);

        return new LoginResponse(accessCookie, refreshCookie, user.getUserStatus());
    }

    @Transactional
    public LoginResponse requestkakaoAccessToken(String code, String redirectUrl,
                                                 CreateUserInfoRequest createUserInfoRequest) {
        User user = checkAndSaveKakaoUser(code, redirectUrl, createUserInfoRequest);
        user.updateLoginTime();
        final int ACCESS_COOKIE_AGE = (int) TimeUnit.MINUTES.toSeconds(30);
        final int REFRESH_COOKIE_AGE = (int) TimeUnit.DAYS.toSeconds(30);

        String token = jwtTokenProvider.createAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        tokenService.synchronizeRefreshToken(user, refreshToken);

        Cookie accessCookie = createCookie("access_token", token, ACCESS_COOKIE_AGE);
        Cookie refreshCookie = createCookie("refresh_token", refreshToken, REFRESH_COOKIE_AGE);

        return new LoginResponse(accessCookie, refreshCookie, user.getUserStatus());
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath("/");
        return cookie;
    }

    private User checkAndSaveKakaoUser(String code, String redirectUrl, CreateUserInfoRequest createUserInfoRequest) {
        KakaoTokenAndUserResponse response = kakaoConnector.requestKakaoUserInfo(code, redirectUrl);
        KakaoTokenResponse tokenResponse = response.getKakaoTokenResponse();
        KakaoUserResponse kakaoUserResponse = response.getKakaoUserResponse();

        String id = kakaoUserResponse.getId().toString();
        User user = userRepository.findByOauthId(id)
                .orElseGet(() -> saveActiveKakaoUser(kakaoUserResponse, createUserInfoRequest));

        checkUserStatus(user);

        Token token = tokenRepository.findByUserId(user.getId())
                .orElseGet(() -> tokenService.saveToken(tokenResponse.getRefreshToken(), user));

        return user;
    }

    private User checkAndSaveUser(String type, String code, String redirectUrl) {
        if (Objects.equals(type, "kakao")) {
            KakaoTokenAndUserResponse response = kakaoConnector.requestKakaoUserInfo(code, redirectUrl);
            KakaoTokenResponse tokenResponse = response.getKakaoTokenResponse();
            KakaoUserResponse kakaoUserResponse = response.getKakaoUserResponse();

            String id = kakaoUserResponse.getId().toString();
            User user = userRepository.findByOauthId(id)
                    .orElseGet(() -> saveKakaoUser(kakaoUserResponse));

            checkUserStatus(user);

            Token token = tokenRepository.findByUserId(user.getId())
                    .orElseGet(() -> tokenService.saveToken(tokenResponse.getRefreshToken(), user));

            return user;
        }
        if (Objects.equals(type, "naver")) {
            NaverTokenAndUserResponse response = naverConnector.requestNaverUserInfo(code);
            NaverTokenResponse tokenResponse = response.getNaverTokenResponse();
            NaverUserResponse naverUserResponse = response.getNaverUserResponse();

            String id = naverUserResponse.getResponse().getId().toString();
            User user = userRepository.findByOauthId(id)
                    .orElseGet(() -> saveNaverUser(naverUserResponse));

            checkUserStatus(user);

            Token token = tokenRepository.findByUserId(user.getId())
                    .orElseGet(() -> tokenService.saveToken(tokenResponse.getRefreshToken(), user));

            return user;
        }
        if (Objects.equals(type, "google")) {
            GoogleUserResponse response = googleConnector.requestGoogleUserInfo(code, redirectUrl)
                    .getBody();
            String id = response.getResourceName();
            return userRepository.findByOauthId(id)
                    .orElseGet(() -> saveGoogleUser(response));
        } else {
            throw new UnsupportedLoginTypeException("지원하지 않는 oauth 로그인입니다.");
        }
    }

    private User saveNaverUser(NaverUserResponse naverUserResponse) {
        String convertedSex = convertSex(naverUserResponse.getResponse().getGender());
        String imageUrl = getDefaultUserImage(convertedSex);
        UserImage savedUserImage = userImageService.saveUserImage(imageUrl);

        User user = User.builder()
                .userStatus(UserStatus.ACTIVATE)
                .oauthId(String.valueOf(naverUserResponse.getResponse().getId()))
                .provider("naver")
                .isAdmin(false)
                .userImage(savedUserImage)
                .nickname(RandomNickName.generateRandomNickname())
                .email(naverUserResponse.getResponse().getEmail())
                .name(naverUserResponse.getResponse().getName())
                .age(Integer.parseInt(naverUserResponse.getResponse().getBirthyear()))
                .sex(convertedSex)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    private String convertSex(String gender) {
        if (gender.equals("M")) {
            return "male";
        } else if (gender.equals("F")) {
            return "female";
        } else {
            return "none";
        }
    }

    private void checkUserStatus(User user) {
        UserStatus userStatus = user.getUserStatus();
        switch (userStatus) {
            case BLOCK -> throw new BlockedUserException("차단된 계정입니다.");
            case DELETE -> throw new DeletedUserException("탈퇴한 계정입니다.");
            case DORMANT -> throw new DormantUserException("휴면 계정입니다.");
        }
    }

    private void saveToken(KakaoTokenResponse tokenResponse, User user) {
        Token token = Token.builder()
                .user(user)
                .oauthToken(tokenResponse.getRefreshToken())
                .build();

        tokenRepository.save(token);
    }

    private User saveGoogleUser(GoogleUserResponse response) {
        String imageUrl = getGoogleUserImage(response);
        UserImage savedUserImage = userImageService.saveUserImage(imageUrl);

        User user = User.builder()
                .userStatus(UserStatus.INACTIVATE)
                .oauthId(response.getResourceName())
                .provider("google")
                .isAdmin(false)
                .userImage(savedUserImage)
                .nickname(RandomNickName.generateRandomNickname())
                .name(response.getNames().get(0).getDisplayName())
                .age(response.getBirthdays().get(0).getDate().getYear())
                .sex(response.getGenders().get(0).getValue())
                .email(response.getEmailAddresses().get(0).getValue())
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    private String getGoogleUserImage(GoogleUserResponse response) {
        String gender = response.getGenders().get(0).getValue();
        if (Objects.equals(gender, "male")) {
            return USER_PROFILE_MALE;
        }
        if (Objects.equals(gender, "female")) {
            return USER_PROFILE_FEMALE;
        }
        return USER_PROFILE_NONE;
    }

    private User saveActiveKakaoUser(KakaoUserResponse kakaoUserResponse, CreateUserInfoRequest createUserInfoRequest) {
        String imageUrl = getDefaultUserImage(createUserInfoRequest.getSex());
        UserImage savedUserImage = userImageService.saveUserImage(imageUrl);

        User user = User.builder()
                .userStatus(UserStatus.PENDING)
                .oauthId(String.valueOf(kakaoUserResponse.getId()))
                .provider("kakao")
                .isAdmin(false)
                .userImage(savedUserImage)
                .name(createUserInfoRequest.getName())
                .sex(createUserInfoRequest.getSex())
                .nickname(RandomNickName.generateRandomNickname())
                .email(kakaoUserResponse.getKakaoAccount().getEmail())
                .name(createUserInfoRequest.getName())
                .age(Integer.valueOf(createUserInfoRequest.getAge()))
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    private User saveKakaoUser(KakaoUserResponse response) {
        String imageUrl = getDefaultUserImage(response.getKakaoAccount().getGender());
        UserImage savedUserImage = userImageService.saveUserImage(imageUrl);

        User user = User.builder()
                .userStatus(UserStatus.PENDING)
                .oauthId(String.valueOf(response.getId()))
                .provider("kakao")
                .isAdmin(false)
                .userImage(savedUserImage)
                .nickname(RandomNickName.generateRandomNickname())
                .email(response.getKakaoAccount().getEmail())
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }


    private String getDefaultUserImage(String gender) {
        if (Objects.equals(gender, "male")) {
            return USER_PROFILE_MALE;
        }
        if (Objects.equals(gender, "female")) {
            return USER_PROFILE_FEMALE;
        }
        return USER_PROFILE_NONE;
    }

    private String getAuthLink(String type, String redirectUrl) {
        return switch (type) {
            case "kakao" -> kakaoProvider.generateAuthUrl(redirectUrl);
            case "google" -> googleProvider.generateAuthUrl(redirectUrl);
            case "naver" -> naverProvider.generateAuthUrl(redirectUrl);
            default -> throw new UnsupportedLoginTypeException("지원하지 않는 oauth 로그인입니다.");
        };
    }

    public AccessTokenResponse reissueAccessToken(Long userId) {
        boolean isExistMember = userRepository.existsById(userId);
        int ACCESS_COOKIE_AGE = (int) TimeUnit.MINUTES.toSeconds(30);
        if (!isExistMember) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        Cookie accessCookie = createCookie("access_token", accessToken, ACCESS_COOKIE_AGE);

        return new AccessTokenResponse(accessCookie);
    }

    @Transactional
    public void logout(HttpServletResponse response, Long userId) {
        tokenService.deleteByMemberId(userId);
        deleteCookie("access_token", "", response);
        deleteCookie("refresh_token", "", response);
    }

    private void deleteCookie(String name, String value, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void revokeToken(String type, String token) throws IOException {
        HttpStatusCode responseCode;
        switch (type) {
            case "kakao" -> responseCode = kakaoConnector.requestRevoke(token);
            case "google" -> responseCode = googleConnector.requestRevoke(token);
            case "naver" -> responseCode = naverConnector.requestRevoke(token);
            default -> throw new UnsupportedLoginTypeException("지원하지 않는 oauth 로그인입니다.");
        }

        if (!responseCode.is2xxSuccessful()) {
            throw new RevokeFailException("회원탈퇴에 실패하였습니다.");
        }
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findByUserId(userId);
        UserImage userImage = userImageRepository.findById(user.getUserImage().getId()).orElseThrow();
        changeToDefaultProfile(user, userImage);
        user.deleteUser();
    }

    private void changeToDefaultProfile(User user, UserImage userImage) {
        String defaultImageUrl = getDefaultProfile(user);
        deleteUserProfileFromS3(userImage, defaultImageUrl);
    }

    private String getDefaultProfile(User user) {
        if (user.getSex() != null) {
            if (user.getSex().equals("male")) {
                return USER_PROFILE_MALE;
            } else {
                return USER_PROFILE_FEMALE;
            }
        }
        return USER_PROFILE_NONE;
    }

    private void deleteUserProfileFromS3(UserImage userImage, String defaultImageUrl) {
        String userImageUrl = userImage.getAddress();
        if (userImageUrl.equals(USER_PROFILE_MALE) || userImageUrl.equals(USER_PROFILE_FEMALE)
                || userImageUrl.equals(
                USER_PROFILE_NONE)) {
            return;
        }
        s3Uploader.deleteImage(userImageUrl);
        userImage.changeToDefaultProfile(defaultImageUrl);
    }

}
