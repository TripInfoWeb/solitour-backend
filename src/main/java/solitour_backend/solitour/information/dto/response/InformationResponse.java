package solitour_backend.solitour.information.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

@Getter
@AllArgsConstructor
public class InformationResponse {

  private Long id;
  private String title;
  private String address;
  private LocalDateTime createdDate;
  private Integer viewCount;
  private String content;
  private String tip;

  private Place place;
  private Category category;
  private ZoneCategory zoneCategory;

}
