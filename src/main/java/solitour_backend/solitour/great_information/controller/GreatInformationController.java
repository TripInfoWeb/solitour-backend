package solitour_backend.solitour.great_information.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.great_information.dto.response.GreatInformationResponse;
import solitour_backend.solitour.great_information.service.GreatInformationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/information/great")
public class GreatInformationController {

  private final GreatInformationService service;

  @GetMapping()
  public ResponseEntity<GreatInformationResponse> getInformationGreatCount(
      @AuthenticationPrincipal Long userId) {
    GreatInformationResponse response = service.getInformationGreatCount(userId);

    return ResponseEntity.ok(response);
  }

  @PostMapping()
  public ResponseEntity<Void> createInformationGreat(
      @AuthenticationPrincipal Long userId, @RequestParam Long infoId) {
    service.createInformationGreat(userId, infoId);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping()
  public ResponseEntity<Void> deleteInformationGreat(@AuthenticationPrincipal Long userId,
      @RequestParam Long greatId) {
    service.deleteInformationGreat(userId, greatId);

    return ResponseEntity.ok().build();
  }
}
