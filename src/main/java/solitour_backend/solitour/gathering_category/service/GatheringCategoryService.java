package solitour_backend.solitour.gathering_category.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.category.dto.request.CategoryRegisterRequest;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.gathering_category.dto.mapper.GatheringCategoryMapper;
import solitour_backend.solitour.gathering_category.dto.request.GatheringCategoryModifyRequest;
import solitour_backend.solitour.gathering_category.dto.response.GatheringCategoryResponse;
import solitour_backend.solitour.gathering_category.entity.GatheringCategory;
import solitour_backend.solitour.gathering_category.repository.GatheringCategoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GatheringCategoryService {

    private final GatheringCategoryRepository gatheringCategoryRepository;
    private final GatheringCategoryMapper gatheringCategoryMapper;

    @Transactional
    public GatheringCategoryResponse registerCategory(CategoryRegisterRequest categoryRegisterRequest) {

        GatheringCategory category = new GatheringCategory(categoryRegisterRequest.getName());
        GatheringCategory saveCategory = gatheringCategoryRepository.save(category);

        return gatheringCategoryMapper.mapToCategoryResponse(saveCategory);
    }


    public GatheringCategoryResponse getCategory(Long id) {
        GatheringCategory category = gatheringCategoryRepository.findById(id)
                .orElseThrow(
                        () -> new CategoryNotExistsException("category not found"));

        return gatheringCategoryMapper.mapToCategoryResponse(category);
    }


    public List<GatheringCategoryResponse> getCategories() {
        List<GatheringCategory> allGatheringCategory = gatheringCategoryRepository.findAll();

        return gatheringCategoryMapper.mapToCategoryResponses(allGatheringCategory);
    }

    @Transactional
    public GatheringCategoryResponse modifyCategory(Long id,
                                                    GatheringCategoryModifyRequest gatheringCategoryModifyRequest) {
        GatheringCategory category = gatheringCategoryRepository.findById(id).orElseThrow();

        category.setName(gatheringCategoryModifyRequest.getName());

        return gatheringCategoryMapper.mapToCategoryResponse(category);
    }

}
