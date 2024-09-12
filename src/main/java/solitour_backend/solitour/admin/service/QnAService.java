package solitour_backend.solitour.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.admin.dto.request.AnswerRegisterRequest;
import solitour_backend.solitour.admin.dto.request.QnARegisterRequest;
import solitour_backend.solitour.admin.dto.request.QuestionRegisterRequest;
import solitour_backend.solitour.admin.dto.response.QnaListResponseDto;
import solitour_backend.solitour.admin.dto.response.QnaResponseDto;
import solitour_backend.solitour.admin.entity.QnA;
import solitour_backend.solitour.admin.entity.QnAMessage;
import solitour_backend.solitour.admin.repository.AnswerRepository;
import solitour_backend.solitour.admin.repository.QnARepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QnAService {

    private final QnARepository qnaRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    public Long createQnA(QnARegisterRequest qnARegisterRequest, Long userId) {

//        title이 1글자보다 많은지
//        categoryName이 있는지
//        content가 1글자보다 큰지

        User user = userRepository.findByUserId(userId);

        QnA _qna = qnaRepository.save(QnA.builder()
                        .title(qnARegisterRequest.getTitle())
                        .status("WAIT")
                        .userId(userId)
                        .categoryName(qnARegisterRequest.getCategoryName())
                .build());

//        _qna가 잘 저장되었는지

        answerRepository.save(QnAMessage.builder()
                .qna(_qna)
                        .content(qnARegisterRequest.getContent())
                        .userId(userId)
                .build());

        return _qna.getId();
    }


    public Page<QnA> getPagedQnAsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));
        return qnaRepository.findByUserId(userId, pageable);
    }

    @Transactional
    public QnaResponseDto getQnAById(Long id, Long userId) {

        QnA qna = qnaRepository.findById(id).orElse(null);
        if (qna == null) {
            // QnA가 존재하지 않는 경우 처리
            return null;
        }

        User user = userRepository.findByUserId(userId);
        if (qna.getUserId() != userId && !user.getIsAdmin()) {
            // 권한이 없는 경우 처리
        }
//        qna.getQnaMessages().size();
        QnaResponseDto qnaResponseDto = QnaResponseDto.builder()
                .id(qna.getId())
                .title(qna.getTitle())
                .createdAt(qna.getCreatedAt())
                .status(qna.getStatus())
                .updatedAt(qna.getUpdatedAt())
                .categoryName(qna.getCategoryName())
                .userId(qna.getUserId())
                .userNickname(user.getNickname())
                .qnaMessages(qna.getQnaMessages())
                .build();
        return qnaResponseDto;
    }

    public void closeQnA(Long id, Long userId) {
        QnA qna = qnaRepository.findById(id).orElse(null);
        if(qna == null) {
//            qna가 없는 경우에도 경고
        }
        if (qna.getUserId() != userId) {
            // 권한이 없는 경우 처리
//            throw new AccessDeniedException("권한이 없습니다.");
        }
        qna.setStatus("CLOSED");
        qnaRepository.save(qna);
    }

    public Page<QnaListResponseDto> getQnAsByPageStatusAndKeyword(int page, String status, String keyword, Long userId) {
        User user = userRepository.findByUserId(userId);
        // 유저가 없거나 관리자가 아니면 밴
        if(!user.getIsAdmin()) {
            return null;
        }
        Pageable pageable = PageRequest.of(page, 10);
        Page<QnA> qnaPage;


        if ("ALL".equals(status)) {
            if (keyword != null && !keyword.isEmpty()) {
                return qnaRepository.findByKeyword(keyword, PageRequest.of(page, 10, Sort.by("updatedAt").ascending()));
            } else {
                return qnaRepository.findAllByOrderByUpdatedAtAsc(PageRequest.of(page, 10, Sort.by("updatedAt").ascending()));
            }
        } else {
            if (keyword != null && !keyword.isEmpty()) {
                return qnaRepository.findByStatusAndKeyword(status, keyword, PageRequest.of(page, 10, Sort.by("updatedAt").ascending()));
            } else {
                return qnaRepository.findByStatusOrderByUpdatedAtAsc(status, PageRequest.of(page, 10, Sort.by("updatedAt").ascending()));
            }
        }
    }

    public QnAMessage createQuestion(QuestionRegisterRequest questionRegisterRequest, Long userid) {
        Optional<QnA> _qna = qnaRepository.findById(questionRegisterRequest.getQnaId());
        if(_qna.isEmpty()) {
//            없을 경우 에러 처리
            return null;
        }
        QnAMessage qnaMessage = answerRepository.save(QnAMessage.builder()
                .content(questionRegisterRequest.getContent())
                .qna(_qna.get())
                .userId(userid)
                .build());
//        새로운 글을 작성했다면 상태를 다시 대기중으로 변경
        _qna.get().setStatus("WAIT");
        qnaRepository.save(_qna.get());
        return qnaMessage;
    }

    public QnAMessage createAnswer(AnswerRegisterRequest answerRegisterRequest, Long userId) {
        Optional<QnA> _qna = qnaRepository.findById(answerRegisterRequest.getQnaId());
        if(_qna.isEmpty()) {
//            없을 경우 에러 처리
            return null;
        }
        User user = userRepository.findByUserId(userId);
        if(!user.getIsAdmin()) {
            return null;
        }

        QnAMessage qnaMessage = answerRepository.save(QnAMessage.builder()
                .content(answerRegisterRequest.getContent())
                .qna(_qna.get())
                .userId(userId)
                .build());
//        답변을 달았으니 변경
        _qna.get().setStatus("ANSWER");
        qnaRepository.save(_qna.get());
        return qnaMessage;
    }
}