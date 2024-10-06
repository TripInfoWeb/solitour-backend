package solitour_backend.solitour.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class AgreeUserInfoRequest {
    private Boolean termConditionAgreement;
    private Boolean privacyPolicyAgreement;
    private String name;
    private String age;
    private String sex;
}
