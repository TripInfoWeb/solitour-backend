package solitour_backend.solitour.user_image.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.place.entity.Place;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

@Getter
@AllArgsConstructor
public class UserImageResponse {

  private String imageUrl;
}
