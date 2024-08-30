package solitour_backend.solitour.gathering_category.dto.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import solitour_backend.solitour.gathering_category.dto.response.GatheringCategoryResponse;
import solitour_backend.solitour.gathering_category.entity.GatheringCategory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface GatheringCategoryMapper {

    GatheringCategoryResponse mapToCategoryResponse(GatheringCategory category);

    List<GatheringCategoryResponse> mapToCategoryResponses(List<GatheringCategory> categories);
}
