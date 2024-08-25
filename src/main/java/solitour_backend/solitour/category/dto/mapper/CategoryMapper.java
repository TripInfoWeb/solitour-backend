package solitour_backend.solitour.category.dto.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.category.dto.response.CategoryResponse;
import solitour_backend.solitour.category.entity.Category;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

    CategoryResponse mapToCategoryResponse(Category category);

    List<CategoryResponse> mapToCategoryResponses(List<Category> categories);
}
