package solitour_backend.solitour.auth.support.naver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.HashMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import solitour_backend.solitour.auth.support.kakao.dto.KakaoUserResponse.Partner;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserResponse {
    private String resultcode;
    private String message;
    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {
        private String id;
        private String email;
        private String name;
        private String nickname;
        private String gender;
        private String age;
        private String birthday;
        private String birthyear;
        private String mobile;
        private String profileImage;
    }

}
