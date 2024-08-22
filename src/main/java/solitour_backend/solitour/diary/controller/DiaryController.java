package solitour_backend.solitour.diary.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;
import solitour_backend.solitour.diary.dto.DiaryRequest;
import solitour_backend.solitour.diary.dto.DiaryResponse;
import solitour_backend.solitour.diary.service.DiaryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class DiaryController {

   private final DiaryService diaryService;

    @Authenticated
    @GetMapping()
    public ResponseEntity<DiaryResponse> getDiary(@AuthenticationPrincipal Long userId) {
        DiaryResponse response = diaryService.getDiary(userId);

        return ResponseEntity.ok(response);
    }

    @Authenticated
    @PostMapping()
    public ResponseEntity<Void> createDiary(@AuthenticationPrincipal Long userId,
                                                 @RequestBody DiaryRequest request) {
        diaryService.createDiary(userId,request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Authenticated
    @PutMapping()
    public ResponseEntity<Void> updateDiary(@AuthenticationPrincipal Long userId,@RequestParam Long diaryId,
                                            @RequestBody DiaryRequest request) {
        diaryService.updateDiary(userId,diaryId,request);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Authenticated
    @DeleteMapping()
    public ResponseEntity<Void> deleteDiary(@AuthenticationPrincipal Long userId,@RequestParam Long diaryId) {
        diaryService.deleteDiary(userId,diaryId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
