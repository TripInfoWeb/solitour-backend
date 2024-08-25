package solitour_backend.solitour.zone_category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ZoneCategoryResponse {

    private ZoneCategoryResponse parentZoneCategory;
    private String name;
}
