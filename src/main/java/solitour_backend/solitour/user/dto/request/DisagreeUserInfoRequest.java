package solitour_backend.solitour.user.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DisagreeUserInfoRequest {
    private Boolean termConditionAgreement;
    private Boolean privacyPolicyAgreement;
}
