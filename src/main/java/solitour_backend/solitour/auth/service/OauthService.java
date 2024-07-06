package solitour_backend.solitour.auth.service;


import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import solitour_backend.solitour.auth.support.kakao.dto.KakaoUserResponse;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.entity.UserRepository;
import solitour_backend.solitour.user.user_status.UserStatus;

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

  public OauthLinkResponse generateAuthUrl(String type, String redirectUrl) {
    String oauthLink = getAuthLink(type, redirectUrl);
    return new OauthLinkResponse(oauthLink);
  }

  @Transactional
  public LoginResponse requestAccessToken(String type, String code, String redirectUrl) {
    User user = checkAndSaveUser(type, code, redirectUrl);
    
    String token = jwtTokenProvider.createAccessToken(user.getId());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

    tokenService.synchronizeRefreshToken(user, refreshToken);

    return new LoginResponse(token, refreshToken);
  }

  private User checkAndSaveUser(String type, String code, String redirectUrl) {
    if(Objects.equals(type, "kakao")){
      KakaoUserResponse response = kakaoConnector.requestKakaoUserInfo(code, redirectUrl).getBody();
      String nickname = response.getKakaoAccount().getProfile().getNickName();
      return userRepository.findByNickname(nickname)
          .orElseGet(() -> saveKakaoUser(response));
    }
    if(Objects.equals(type, "google")){
      GoogleUserResponse response = googleConnector.requestGoogleUserInfo(code, redirectUrl).getBody();
      String email = response.getEmail();
      return userRepository.findByEmail(email)
          .orElseGet(() -> saveGoogleUser(response));
    }
    else{
      throw new RuntimeException("지원하지 않는 oauth 타입입니다.");
    }
  }
  private User saveGoogleUser(GoogleUserResponse response) {
    User user = User.builder()
        .userStatus(UserStatus.ACTIVATE)
        .oauthId(response.getId())
        .provider("google")
        .isAdmin(false)
        .nickname(RandomNickName.generateRandomNickname())
        .name(response.getName())
        .email(response.getEmail())
        .createdAt(LocalDateTime.now())
        .build();
    return userRepository.save(user);
  }

  private User saveKakaoUser(KakaoUserResponse response) {
    User user = User.builder()
        .userStatus(UserStatus.ACTIVATE)
        .oauthId(String.valueOf(response.getId()))
        .provider("kakao")
        .isAdmin(false)
        .name(response.getKakaoAccount().getName())
        .nickname(response.getKakaoAccount().getProfile().getNickName())
        .age(Integer.valueOf(response.getKakaoAccount().getBirthYear()))
        .sex(response.getKakaoAccount().getGender())
        .email(response.getKakaoAccount().getEmail())
        .createdAt(LocalDateTime.now())
        .build();
    return userRepository.save(user);
  }

  private String getAuthLink(String type, String redirectUrl) {
    return switch (type) {
      case "kakao" -> kakaoProvider.generateAuthUrl(redirectUrl);
      case "google" -> googleProvider.generateAuthUrl(redirectUrl);
      default -> throw new RuntimeException("지원하지 않는 oauth 타입입니다.");
    };
  }

  public AccessTokenResponse reissueAccessToken(Long userId) {
    boolean isExistMember = userRepository.existsById(userId);
    if (!isExistMember) {
      throw new RuntimeException("유효하지 않은 토큰입니다.");
    }
    String accessToken = jwtTokenProvider.createAccessToken(userId);
    return new AccessTokenResponse(accessToken);
  }

  @Transactional
  public void logout(Long memberId) {
    tokenService.deleteByMemberId(memberId);
  }
}
