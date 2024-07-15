package solitour_backend.solitour.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CategoryGetResponse {

  private Long id;
  private String name;
  private List<CategoryResponse> childrenCategories;
}
