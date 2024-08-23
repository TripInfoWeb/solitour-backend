package solitour_backend.solitour.diary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.diary.dto.DiaryListResponse;
import solitour_backend.solitour.diary.dto.DiaryRequest;
import solitour_backend.solitour.diary.dto.DiaryResponse;
import solitour_backend.solitour.diary.entity.Diary;
import solitour_backend.solitour.diary.service.DiaryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @Authenticated
    @GetMapping()
    public ResponseEntity<DiaryListResponse> getAllDiary(@AuthenticationPrincipal Long userId) {
        DiaryListResponse response = diaryService.getAllDiary(userId);

        return ResponseEntity.ok(response);
    }

    @Authenticated
    @GetMapping("/{diaryId}")
    public ResponseEntity<DiaryResponse> getDiary(@AuthenticationPrincipal Long userId, @PathVariable Long diaryId) {
        DiaryResponse response = diaryService.getDiary(userId, diaryId);

        return ResponseEntity.ok(response);
    }

    @Authenticated
    @PostMapping()
    public ResponseEntity<Long> createDiary(@AuthenticationPrincipal Long userId,
                                            @RequestBody DiaryRequest request) {
        Long diaryId = diaryService.createDiary(userId, request);

        return ResponseEntity.ok(diaryId);
    }

    @Authenticated
    @PutMapping()
    public ResponseEntity<Long> updateDiary(@AuthenticationPrincipal Long userId, @RequestParam Long diaryId,
                                            @RequestBody DiaryRequest request) {
        diaryService.updateDiary(userId, diaryId, request);

        return ResponseEntity.ok(diaryId);
    }

    @Authenticated
    @DeleteMapping()
    public ResponseEntity<Void> deleteDiary(@AuthenticationPrincipal Long userId, @RequestParam Long diaryId) {
        diaryService.deleteDiary(userId, diaryId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
