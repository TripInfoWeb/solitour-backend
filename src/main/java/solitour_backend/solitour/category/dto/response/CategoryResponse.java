package solitour_backend.solitour.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import solitour_backend.solitour.category.entity.Category;

@Getter
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private Category parentCategory;
    private String name;

}
