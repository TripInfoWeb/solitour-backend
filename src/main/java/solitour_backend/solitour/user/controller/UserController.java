package solitour_backend.solitour.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.service.OauthService;
import solitour_backend.solitour.auth.service.TokenService;
import solitour_backend.solitour.auth.support.CookieExtractor;
import solitour_backend.solitour.auth.support.google.GoogleConnector;
import solitour_backend.solitour.auth.support.kakao.KakaoConnector;
import solitour_backend.solitour.user.dto.UpdateAgeAndSex;
import solitour_backend.solitour.user.dto.UpdateNicknameRequest;
import solitour_backend.solitour.user.exception.NicknameAlreadyExistsException;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.user.service.UserService;
import solitour_backend.solitour.user.service.dto.response.UserInfoResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OauthService oauthservice;
    private final KakaoConnector kakaoConnector;
    private final GoogleConnector googleConnector;
    private final TokenService tokenService;

    @Authenticated
    @GetMapping("/info")
    public ResponseEntity<UserInfoResponse> retrieveUserInfo(@AuthenticationPrincipal Long userId) {
        UserInfoResponse response = userService.retrieveUserInfo(userId);

        return ResponseEntity.ok(response);
    }

    @Authenticated
    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(@AuthenticationPrincipal Long userId, @RequestBody UpdateNicknameRequest request) {
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

    @Authenticated
    @PutMapping("/age-sex")
    public ResponseEntity<String> updateAgeAndSex(@AuthenticationPrincipal Long userId, @RequestBody UpdateAgeAndSex request) {
        try {
            userService.updateAgeAndSex(userId, request.age(),request.sex());
            return ResponseEntity.ok("Age and Sex updated successfully");
        } catch (UserNotExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal error occurred");
        }
    }

    @Authenticated
    @DeleteMapping()
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal Long id, @RequestParam String type, @RequestParam String code, @RequestParam String redirectUrl) {
        String token  = getOauthAccessToken(type,code,redirectUrl);

        try {
            oauthservice.revokeToken(token);

            userService.deleteUser(id);
            oauthservice.logout(id);

            return ResponseEntity.ok("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    private String getOauthAccessToken(String type, String code, String redirectUrl){
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
