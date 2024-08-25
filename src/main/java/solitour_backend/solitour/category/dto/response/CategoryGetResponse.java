package solitour_backend.solitour.category.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryGetResponse {

    private Long id;
    private String name;
    private List<CategoryResponse> childrenCategories;
}
