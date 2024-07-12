package solitour_backend.solitour.information.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.information.dto.request.InformationRegisterRequest;
import solitour_backend.solitour.information.dto.response.InformationDetailResponse;
import solitour_backend.solitour.information.dto.response.InformationResponse;
import solitour_backend.solitour.information.service.InformationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/informations")
public class InformationController {

  private final InformationService informationService;

  @PostMapping
  public ResponseEntity<InformationResponse> createInformation(
      @Valid @RequestPart("request") InformationRegisterRequest informationRegisterRequest,
      @RequestPart("thumbNailImage") MultipartFile thumbnail,
      @RequestPart("contentImages") List<MultipartFile> contentImages,
      BindingResult bindingResult) {
    Utils.validationRequest(bindingResult);
    InformationResponse informationResponse = informationService.registerInformation(
        informationRegisterRequest, thumbnail, contentImages);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(informationResponse);
  }

  @GetMapping("/{informationId}")
  public ResponseEntity<InformationDetailResponse> getDetailInformation(
      @PathVariable Long informationId) {
    InformationDetailResponse informationDetailResponse = informationService.getDetailInformation(
        informationId);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(informationDetailResponse);
  }
}
