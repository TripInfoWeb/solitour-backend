package solitour_backend.solitour.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import solitour_backend.solitour.admin.dto.request.AnswerRegisterRequest;
import solitour_backend.solitour.admin.dto.request.QnARegisterRequest;
import solitour_backend.solitour.admin.dto.request.QuestionRegisterRequest;
import solitour_backend.solitour.admin.dto.response.QnaListResponseDto;
import solitour_backend.solitour.admin.dto.response.QnaResponseDto;
import solitour_backend.solitour.admin.entity.QnAMessage;
import solitour_backend.solitour.admin.entity.QnA;
import solitour_backend.solitour.admin.service.QnAService;
import solitour_backend.solitour.auth.config.Authenticated;
import solitour_backend.solitour.auth.config.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/qna")
public class QnAController {

    @Autowired
    private QnAService qnaService;

    @Authenticated
    @GetMapping("/manage-list")
    public ResponseEntity<Page<QnaListResponseDto>> getPagedQnAsByUserId(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String keyword
    ) {
        Page<QnaListResponseDto> qnaPage = qnaService.getQnAsByPageStatusAndKeyword(page, status, keyword, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(qnaPage);
    }

    @Authenticated
    @PostMapping
    public ResponseEntity<Long> createQnA(@AuthenticationPrincipal Long userId, @RequestBody QnARegisterRequest qnARegisterRequest) {
        Long qnaId = qnaService.createQnA(qnARegisterRequest, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(qnaId);
    }

    @Authenticated
    @GetMapping
    public Page<QnA> getPagedQnAsByUserId(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return qnaService.getPagedQnAsByUserId(userId, page, size);
    }

    @Authenticated
    @GetMapping("/{id}")
    public ResponseEntity<QnaResponseDto> getQnAById(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        QnaResponseDto qna = qnaService.getQnAById(id, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(qna);
    }

    @Authenticated
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> closeQnAStatus(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        qnaService.closeQnA(id, userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @Authenticated
    @PostMapping("/question")
    public ResponseEntity<QnAMessage> createQuestion(@AuthenticationPrincipal Long userId, @RequestBody QuestionRegisterRequest questionRegisterRequest) {
        QnAMessage qnaMessage = qnaService.createQuestion(questionRegisterRequest, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(qnaMessage);
    }

    @Authenticated
    @PostMapping("/answer")
    public ResponseEntity<QnAMessage> createAnswer(@AuthenticationPrincipal Long userId, @RequestBody AnswerRegisterRequest answerRegisterRequest) {
        QnAMessage qnaMessage = qnaService.createAnswer(answerRegisterRequest, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(qnaMessage);
    }
}