package solitour_backend.solitour.book_mark_information.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformation;
import solitour_backend.solitour.book_mark_information.service.BookMarkInformationService;
import solitour_backend.solitour.book_mark_information.service.dto.response.BookMarkInformationResponse;

@Authenticated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmark/information")
public class BookMarkInformationController {

    private final BookMarkInformationService service;

    @Transactional
    @PostMapping()
    public ResponseEntity<Long> createUserBookmark(
            @AuthenticationPrincipal Long userId, @RequestParam Long infoId) {
        BookMarkInformation bookMarkInformation = service.createUserBookmark(userId, infoId);

        return ResponseEntity.ok(bookMarkInformation.getId());
    }

    @Transactional
    @DeleteMapping()
    public ResponseEntity<Void> deleteUserBookmark(@AuthenticationPrincipal Long userId,
                                                   @RequestParam Long infoId) {
        service.deleteUserBookmark(userId, infoId);

        return ResponseEntity.noContent().build();
    }
}
