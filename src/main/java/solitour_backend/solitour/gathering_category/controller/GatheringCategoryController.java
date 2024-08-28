package solitour_backend.solitour.gathering_category.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.category.dto.request.CategoryRegisterRequest;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.gathering_category.dto.request.GatheringCategoryModifyRequest;
import solitour_backend.solitour.gathering_category.dto.response.GatheringCategoryResponse;
import solitour_backend.solitour.gathering_category.service.GatheringCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories/gathering")
public class GatheringCategoryController {

    private final GatheringCategoryService gatheringCategoryService;

    @GetMapping
    public ResponseEntity<List<GatheringCategoryResponse>> getAllCategories() {
        List<GatheringCategoryResponse> parentCategories = gatheringCategoryService.getCategories();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(parentCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GatheringCategoryResponse> getCategory(@PathVariable Long id) {
        GatheringCategoryResponse category = gatheringCategoryService.getCategory(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(category);
    }

    @PostMapping
    public ResponseEntity<GatheringCategoryResponse> registerCategory(
            @Valid @RequestBody CategoryRegisterRequest categoryRegisterRequest,
            BindingResult bindingResult) {
        Utils.validationRequest(bindingResult);

        GatheringCategoryResponse categoryResponse = gatheringCategoryService.registerCategory(
                categoryRegisterRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryResponse);

    }

    @PutMapping("/{id}")
    public ResponseEntity<GatheringCategoryResponse> modifyCategory(
            @Valid @RequestBody GatheringCategoryModifyRequest gatheringCategoryModifyRequest,
            BindingResult bindingResult,
            @PathVariable Long id) {
        if (bindingResult.hasErrors()) {
            throw new RequestValidationFailedException(bindingResult);
        }
        GatheringCategoryResponse categoryResponse = gatheringCategoryService.modifyCategory(id,
                gatheringCategoryModifyRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryResponse);
    }


}
