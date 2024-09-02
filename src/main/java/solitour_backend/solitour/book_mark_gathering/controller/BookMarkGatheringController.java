package solitour_backend.solitour.book_mark_gathering.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.book_mark_gathering.dto.response.BookMarkGatheringResponse;
import solitour_backend.solitour.book_mark_gathering.service.BookMarkGatheringService;
import solitour_backend.solitour.book_mark_information.service.BookMarkInformationService;
import solitour_backend.solitour.book_mark_information.service.dto.response.BookMarkInformationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmark/gathering")
public class BookMarkGatheringController {

    private final BookMarkGatheringService service;

    @GetMapping()
    public ResponseEntity<BookMarkGatheringResponse> getUserBookmark(
            @AuthenticationPrincipal Long userId) {
        BookMarkGatheringResponse response = service.getUserBookmark(userId);

        return ResponseEntity.ok(response);
    }

    @Transactional
    @PostMapping()
    public ResponseEntity<BookMarkGatheringResponse> createUserBookmark(
            @AuthenticationPrincipal Long userId, @RequestParam Long gatheringId) {
        service.createUserBookmark(userId, gatheringId);

        return ResponseEntity.ok().build();
    }

    @Transactional
    @DeleteMapping()
    public ResponseEntity<Void> deleteUserBookmark(@AuthenticationPrincipal Long userId,
                                                   @RequestParam Long gatheringId) {
        service.deleteUserBookmark(userId, gatheringId);

        return ResponseEntity.ok().build();
    }
}