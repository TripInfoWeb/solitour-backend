package solitour_backend.solitour.admin.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.gathering_category.entity.GatheringCategory;
import solitour_backend.solitour.category.dto.response.CategoryResponse;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface GatheringCategoryMapper {

    CategoryResponse mapToCategoryResponse(GatheringCategory category);

    List<CategoryResponse> mapToCategoryResponses(List<GatheringCategory> categories);
}