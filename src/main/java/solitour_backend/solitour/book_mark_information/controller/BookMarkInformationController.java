package solitour_backend.solitour.book_mark_information.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.book_mark_information.service.BookMarkInformationService;
import solitour_backend.solitour.book_mark_information.service.dto.response.BookMarkInformationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmark/information")
public class BookMarkInformationController {

  private final BookMarkInformationService service;

  @GetMapping()
  public ResponseEntity<BookMarkInformationResponse> getUserBookmark(
      @AuthenticationPrincipal Long userId) {
    BookMarkInformationResponse response = service.getUserBookmark(userId);

    return ResponseEntity.ok(response);
  }

  @PostMapping()
  public ResponseEntity<BookMarkInformationResponse> createUserBookmark(
      @AuthenticationPrincipal Long userId, @RequestParam Long infoId) {
    service.createUserBookmark(userId, infoId);

    return ResponseEntity.ok().build();
  }

  @DeleteMapping()
  public ResponseEntity<Void> deleteUserBookmark(@AuthenticationPrincipal Long userId,
      @RequestParam Long bookMarkId) {
    service.deleteUserBookmark(bookMarkId);

    return ResponseEntity.ok().build();
  }
}
