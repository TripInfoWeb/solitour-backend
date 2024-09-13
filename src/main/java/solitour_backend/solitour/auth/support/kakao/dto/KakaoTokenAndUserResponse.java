package solitour_backend.solitour.auth.support.kakao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoTokenAndUserResponse {

    private KakaoTokenResponse kakaoTokenResponse;
    private KakaoUserResponse kakaoUserResponse;
}
