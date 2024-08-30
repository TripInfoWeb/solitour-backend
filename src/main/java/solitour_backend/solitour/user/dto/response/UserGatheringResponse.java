package solitour_backend.solitour.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserGatheringResponse {
    private Long id;
    private String profileUrl;
    private String nickname;
    private Integer age;
    private String sex;
}
