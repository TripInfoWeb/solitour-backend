package solitour_backend.solitour.user.controller;


import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.service.OauthService;
import solitour_backend.solitour.auth.support.google.GoogleConnector;
import solitour_backend.solitour.auth.support.kakao.KakaoConnector;
import solitour_backend.solitour.gathering.dto.response.GatheringApplicantResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.user.dto.UpdateAgeAndSex;
import solitour_backend.solitour.user.dto.UpdateNicknameRequest;
import solitour_backend.solitour.user.exception.NicknameAlreadyExistsException;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.user.service.UserService;
import solitour_backend.solitour.user.service.dto.response.UserInfoResponse;

@Authenticated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OauthService oauthservice;
    private final KakaoConnector kakaoConnector;
    private final GoogleConnector googleConnector;

    public static final int PAGE_SIZE = 6;

    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> retrieveUserInfo(@AuthenticationPrincipal Long userId) {
        UserInfoResponse response = userService.retrieveUserInfo(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(@AuthenticationPrincipal Long userId,
                                                 @RequestBody UpdateNicknameRequest request) {
        try {
            userService.updateNickname(userId, request.nickname());
            return ResponseEntity.ok("Nickname updated successfully");
        } catch (UserNotExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (NicknameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Nickname already exists");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred");
        }
    }

    @PutMapping("/age-sex")
    public ResponseEntity<String> updateAgeAndSex(@AuthenticationPrincipal Long userId,
                                                  @RequestBody UpdateAgeAndSex request) {
        try {
            userService.updateAgeAndSex(userId, request.age(), request.sex());
            return ResponseEntity.ok("Age and Sex updated successfully");
        } catch (UserNotExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred");
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(@AuthenticationPrincipal Long userId,
                                                  @RequestPart(value = "userProfile", required = false) MultipartFile userProfile) {
        userService.updateUserProfile(userId, userProfile);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteUser(HttpServletResponse response, @AuthenticationPrincipal Long id,
                                             @RequestParam String type,
                                             @RequestParam String code, @RequestParam String redirectUrl) {
        String token = getOauthAccessToken(type, code, redirectUrl);

        try {
            oauthservice.revokeToken(type, token);

            oauthservice.logout(response, id);
            userService.deleteUser(id);

            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/mypage/information/owner")
    public ResponseEntity<Page<InformationBriefResponse>> retrieveInformationOwner(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long userId) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> response = userService.retrieveInformationOwner(pageable, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/information/bookmark")
    public ResponseEntity<Page<InformationBriefResponse>> retrieveInformationBookmark(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long userId) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationBriefResponse> response = userService.retrieveInformationBookmark(pageable,
                userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/gathering/host")
    public ResponseEntity<Page<GatheringBriefResponse>> retrieveGatheringHost(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long userId) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<GatheringBriefResponse> response = userService.retrieveGatheringHost(pageable, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/gathering/bookmark")
    public ResponseEntity<Page<GatheringBriefResponse>> retrieveGatheringBookmark(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long userId) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<GatheringBriefResponse> response = userService.retrieveGatheringBookmark(pageable,
                userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mypage/gathering/applicant")
    public ResponseEntity<Page<GatheringApplicantResponse>> retrieveGatheringApplicant(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long userId) {

        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<GatheringApplicantResponse> response = userService.retrieveGatheringApplicant(pageable,
                userId);

        return ResponseEntity.ok(response);
    }

    private String getOauthAccessToken(String type, String code, String redirectUrl) {
        String token = "";
        switch (type) {
            case "kakao" -> {
                token = kakaoConnector.requestAccessToken(code, redirectUrl);
            }
            case "google" -> {
                token = googleConnector.requestAccessToken(code, redirectUrl);
            }
            default -> throw new RuntimeException("Unsupported oauth type");
        }
        return token;
    }

}
