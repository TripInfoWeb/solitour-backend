package solitour_backend.solitour.category.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.category.dto.mapper.CategoryMapper;
import solitour_backend.solitour.category.dto.request.CategoryModifyRequest;
import solitour_backend.solitour.category.dto.request.CategoryRegisterRequest;
import solitour_backend.solitour.category.dto.response.CategoryGetResponse;
import solitour_backend.solitour.category.dto.response.CategoryResponse;
import solitour_backend.solitour.category.entity.Category;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.category.repository.CategoryRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public CategoryResponse registerCategory(CategoryRegisterRequest categoryRegisterRequest) {
        Category parentCategoryEntity;
        if (Objects.isNull(categoryRegisterRequest.getParentCategory())) {
            parentCategoryEntity = null;
        } else {
            parentCategoryEntity = categoryRepository.findById(
                            categoryRegisterRequest.getParentCategory())
                    .orElseThrow(
                            () -> new CategoryNotExistsException("Parent category not found"));
        }

        Category category = new Category(parentCategoryEntity, categoryRegisterRequest.getName());
        Category saveCategory = categoryRepository.save(category);

        return categoryMapper.mapToCategoryResponse(saveCategory);
    }


    public CategoryResponse getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(
                        () -> new CategoryNotExistsException("category not found"));

        return categoryMapper.mapToCategoryResponse(category);
    }


    public List<CategoryResponse> getChildrenCategories(Long id) {
        List<Category> childrenCategories = categoryRepository.findAllByParentCategoryId(id);

        return categoryMapper.mapToCategoryResponses(childrenCategories);
    }

    public List<CategoryGetResponse> getParentCategories() {
        List<Category> parentCategories = categoryRepository.findAllByParentCategoryId(null);
        List<CategoryGetResponse> categoryGetResponses = new ArrayList<>();

        for (Category category : parentCategories) {
            List<CategoryResponse> childrenCategories = getChildrenCategories(category.getId());
            categoryGetResponses.add(
                    new CategoryGetResponse(category.getId(), category.getName(), childrenCategories));
        }

        return categoryGetResponses;
    }

    @Transactional
    public CategoryResponse modifyCategory(Long id, CategoryModifyRequest categoryModifyRequest) {
        Category parentCategoryEntity;
        if (Objects.isNull(categoryModifyRequest.getParentCategory())) {
            parentCategoryEntity = null;
        } else {
            parentCategoryEntity = categoryRepository.findById(
                            categoryModifyRequest.getParentCategory())
                    .orElseThrow(
                            () -> new CategoryNotExistsException("Parent category not found"));
        }

        Category category = categoryRepository.findById(id).orElseThrow();

        category.setName(categoryModifyRequest.getName());
        category.setParentCategory(parentCategoryEntity);

        return categoryMapper.mapToCategoryResponse(category);
    }

}
