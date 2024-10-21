package solitour_backend.solitour.information_comment.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.auth.support.JwtTokenProvider;
import solitour_backend.solitour.error.Utils;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information_comment.dto.request.InformationCommentRequest;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentListResponse;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentResponse;
import solitour_backend.solitour.information_comment.service.InformationCommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/informations/comments")
public class InformationCommentController {

    private final InformationCommentService informationCommentService;


    @Authenticated
    @PostMapping("/{informationId}")
    public ResponseEntity<InformationCommentResponse> createInformationComment(@AuthenticationPrincipal Long userId,
                                                                               @PathVariable Long informationId,
                                                                               @Valid @RequestBody InformationCommentRequest informationCommentRequest) {

        InformationCommentResponse informationCommentResponse = informationCommentService.createInformationComment(userId, informationId, informationCommentRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(informationCommentResponse);
    }

    @GetMapping("/{informationId}")
    public ResponseEntity<Page<InformationCommentListResponse>> getPageInformationComment(@RequestParam(defaultValue = "0") int page,
                                                                                          @PathVariable Long informationId) {

        final int PAGE_SIZE = 5;
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<InformationCommentListResponse> pageInformation = informationCommentService.getPageInformationComment(pageable, informationId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(pageInformation);
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

