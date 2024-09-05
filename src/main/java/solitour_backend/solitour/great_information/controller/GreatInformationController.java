package solitour_backend.solitour.great_information.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.great_information.dto.response.GreatInformationResponse;
import solitour_backend.solitour.great_information.entity.GreatInformation;
import solitour_backend.solitour.great_information.service.GreatInformationService;

@Authenticated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/information/great")
public class GreatInformationController {

    private final GreatInformationService service;

    @PostMapping()
    public ResponseEntity<Long> createInformationGreat(@AuthenticationPrincipal Long userId,
                                                       @RequestParam Long infoId) {
        GreatInformation greatInformation = service.createInformationGreat(userId, infoId);

        return ResponseEntity.ok(greatInformation.getId());
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteInformationGreat(@AuthenticationPrincipal Long userId,
                                                       @RequestParam Long infoId) {
        service.deleteInformationGreat(userId, infoId);

        return ResponseEntity.noContent().build();
    }
}
