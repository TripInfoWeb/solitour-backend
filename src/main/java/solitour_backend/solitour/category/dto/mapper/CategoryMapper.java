package solitour_backend.solitour.category.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.category.dto.response.CategoryResponse;
import solitour_backend.solitour.category.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CategoryMapper {

  CategoryResponse mapToCategoryResponse(Category category);

  List<CategoryResponse> mapToCategoryResponses(List<Category> categories);
}
