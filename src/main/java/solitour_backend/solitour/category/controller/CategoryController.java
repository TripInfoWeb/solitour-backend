package solitour_backend.solitour.category.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import solitour_backend.solitour.category.dto.request.CategoryModifyRequest;
import solitour_backend.solitour.category.dto.request.CategoryRegisterRequest;
import solitour_backend.solitour.category.dto.response.CategoryGetResponse;
import solitour_backend.solitour.category.dto.response.CategoryResponse;
import solitour_backend.solitour.category.service.CategoryService;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryGetResponse>> getAllCategories() {
    List<CategoryGetResponse> parentCategories = categoryService.getParentCategories();

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(parentCategories);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> getCategory(@PathVariable Long id) {
    CategoryResponse category = categoryService.getCategory(id);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(category);
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> registerCategory(
      @Valid @RequestBody CategoryRegisterRequest categoryRegisterRequest,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RequestValidationFailedException(bindingResult);
    }
    CategoryResponse categoryResponse = categoryService.registerCategory(categoryRegisterRequest);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(categoryResponse);

  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> modifyCategory(
      @Valid @RequestBody CategoryModifyRequest categoryModifyRequest, BindingResult bindingResult,
      @PathVariable Long id) {
    if (bindingResult.hasErrors()) {
      throw new RequestValidationFailedException(bindingResult);
    }
    CategoryResponse categoryResponse = categoryService.modifyCategory(id, categoryModifyRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(categoryResponse);
  }


}
