package solitour_backend.solitour.great_gathering.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.great_gathering.entity.GreatGathering;
import solitour_backend.solitour.great_gathering.service.GreatGatheringService;
import solitour_backend.solitour.great_information.entity.GreatInformation;
import solitour_backend.solitour.great_information.service.GreatInformationService;

@Authenticated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gathering/great")
public class GreatGatheringController {

    private final GreatGatheringService service;

    @PostMapping()
    public ResponseEntity<Long> createGatheringGreat(@AuthenticationPrincipal Long userId,
                                                       @RequestParam Long gatheringId) {
        GreatGathering greatGathering = service.createGatheringGreat(userId, gatheringId);

        return ResponseEntity.ok(greatGathering.getId());
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteGatheringGreat(@AuthenticationPrincipal Long userId,
                                                       @RequestParam Long gatheringId) {
        service.deleteGatheringGreat(userId, gatheringId);

        return ResponseEntity.noContent().build();
    }
}
