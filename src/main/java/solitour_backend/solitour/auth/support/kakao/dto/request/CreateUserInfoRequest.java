package solitour_backend.solitour.auth.support.kakao.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CreateUserInfoRequest {
    private String name;
    private String age;
    private String sex;
}
