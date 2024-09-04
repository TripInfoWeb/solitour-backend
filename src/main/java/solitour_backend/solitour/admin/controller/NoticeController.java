package solitour_backend.solitour.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solitour_backend.solitour.admin.dto.request.NoticeModifyRequest;
import solitour_backend.solitour.admin.dto.request.NoticeRegisterRequest;
import solitour_backend.solitour.admin.entity.Notice;
import solitour_backend.solitour.admin.service.NoticeService;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

    private final NoticeService noticeService;

    // Create a new notice
    @Authenticated
    @PostMapping
    public ResponseEntity<Long> createNotice(@AuthenticationPrincipal Long userId, @RequestBody NoticeRegisterRequest noticeRegisterRequest) {
        Long id = noticeService.createNotice(noticeRegisterRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // Retrieve a list of notices (excluding deleted ones)
    @GetMapping
    public ResponseEntity<Page<Notice>> getNotices(@RequestParam(defaultValue = "0") int page) {
    Page<Notice> notices = noticeService.getNotices(page);
    return ResponseEntity.ok(notices);
    }

    // Retrieve a list of notices (excluding deleted ones)
    @Authenticated
    @GetMapping("/admin")
    public ResponseEntity<Page<Notice>> getNoticesForAdmin(@AuthenticationPrincipal Long userId, @RequestParam(defaultValue = "0") int page) {
        Page<Notice> notices = noticeService.getNoticesForAdmin(page, userId);
        return ResponseEntity.ok(notices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notice> getNotice(@PathVariable Long id) {
        Notice notice = noticeService.getNotice(id);
        return ResponseEntity.ok(notice);
    }

    @Authenticated
    @GetMapping("/admin/{id}")
    public ResponseEntity<Notice> getNoticeForAdmin(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        Notice notice = noticeService.getNoticeForAdmin(userId, id);
        return ResponseEntity.ok(notice);
    }

    @Authenticated
    @PutMapping
    public ResponseEntity updateNotice(@AuthenticationPrincipal Long userId, @RequestBody NoticeModifyRequest noticeModifyRequest) {
        boolean result = noticeService.updateNotice(userId, noticeModifyRequest);
        if(result) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @Authenticated
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotice(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        noticeService.deleteNotice(userId, id);
        return ResponseEntity.noContent().build();
    }
}
