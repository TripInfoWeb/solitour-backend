package solitour_backend.solitour.information_comment.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.support.JwtTokenProvider;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.information_comment.dto.request.InformationCommentRequest;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentResponse;
import solitour_backend.solitour.information_comment.service.InformationCommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/information/comment")
public class InformationCommentController {

    private final InformationCommentService informationCommentService;

    @Authenticated
    @PostMapping
    public ResponseEntity<InformationCommentResponse> createInformationComment(@AuthenticationPrincipal Long userId,
                                                                               @Valid @RequestBody InformationCommentRequest informationCommentRequest) {

        InformationCommentResponse informationCommentResponse = informationCommentService.createInformationComment(userId, informationCommentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(informationCommentResponse);
    }

    @Authenticated
    @PutMapping("/{informationCommentId}")
    public ResponseEntity<Void> modifyInformationComment(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long informationCommentId,
                                                         @Valid @RequestBody InformationCommentRequest informationCommentRequest) {

        informationCommentService.modifyInformationComment(userId, informationCommentId, informationCommentRequest);

        return ResponseEntity.noContent().build();
    }

    @Authenticated
    @DeleteMapping("/{informationCommentId}")
    public ResponseEntity<Void> deleteInformationComment(@AuthenticationPrincipal Long userId,
                                                         @PathVariable Long informationCommentId) {
        informationCommentService.deleteInformationComment(userId, informationCommentId);

        return ResponseEntity.noContent().build();
    }
}

