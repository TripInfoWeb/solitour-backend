package solitour_backend.solitour.zone_category.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.zone_category.dto.request.ZoneCategoryModifyRequest;
import solitour_backend.solitour.zone_category.dto.request.ZoneCategoryRegisterRequest;
import solitour_backend.solitour.zone_category.dto.response.ZoneCategoryResponse;
import solitour_backend.solitour.zone_category.service.ZoneCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/zoneCategories")
public class ZoneCategoryController {

  private final ZoneCategoryService zoneCategoryService;

  @GetMapping("/{id}")
  public ResponseEntity<ZoneCategoryResponse> getZoneCategory(@PathVariable Long id) {
    ZoneCategoryResponse zoneCategoryResponse = zoneCategoryService.getZoneCategoryById(id);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(zoneCategoryResponse);
  }

  @PostMapping
  public ResponseEntity<ZoneCategoryResponse> registerZoneCategory(
      @Valid @RequestBody ZoneCategoryRegisterRequest zoneCategoryRegisterRequest,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RequestValidationFailedException(bindingResult);
    }
    ZoneCategoryResponse zoneCategoryResponse = zoneCategoryService.registerZoneCategory(
        zoneCategoryRegisterRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(zoneCategoryResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ZoneCategoryResponse> modifyZoneCategory(@PathVariable Long id,
      @Valid @RequestBody ZoneCategoryModifyRequest zoneCategoryModifyRequest,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      throw new RequestValidationFailedException(bindingResult);
    }
    ZoneCategoryResponse zoneCategoryResponse = zoneCategoryService.modifyZoneCategory(id,
        zoneCategoryModifyRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(zoneCategoryResponse);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteZoneCategory(@PathVariable Long id) {
    zoneCategoryService.deleteZoneCategory(id);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }


}
