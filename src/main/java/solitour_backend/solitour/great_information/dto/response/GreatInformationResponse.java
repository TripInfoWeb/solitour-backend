package solitour_backend.solitour.great_information.dto.response;

import lombok.Getter;

@Getter
public class GreatInformationResponse {

    private final int greatInformationCount;

    public GreatInformationResponse(int greatInformationCount) {
        this.greatInformationCount = greatInformationCount;
    }
}
