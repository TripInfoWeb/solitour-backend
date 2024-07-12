package solitour_backend.solitour.zone_category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

@Getter
@AllArgsConstructor
public class ZoneCategoryResponse {

  private Long id;
  private ZoneCategory parentZoneCategory;
  private String name;
}
