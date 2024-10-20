package solitour_backend.solitour.information_comment.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.exception.InformationNotExistsException;
import solitour_backend.solitour.information.repository.InformationRepository;
import solitour_backend.solitour.information_comment.dto.request.InformationCommentRequest;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentResponse;
import solitour_backend.solitour.information_comment.entity.InformationComment;
import solitour_backend.solitour.information_comment.exception.CommentNotOwnerException;
import solitour_backend.solitour.information_comment.exception.InformationCommentNotExistsException;
import solitour_backend.solitour.information_comment.repository.InformationCommentRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InformationCommentService {

    private final InformationRepository informationRepository;
    private final UserRepository userRepository;
    private final InformationCommentRepository informationCommentRepository;

    @Transactional
    public InformationCommentResponse createInformationComment(Long userId, @Valid InformationCommentRequest informationCommentRequest) {
        Information information = informationRepository.findByUserId(userId)
                .orElseThrow(() -> new InformationNotExistsException("해당하는 정보가 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotExistsException("해당하는 사용자가 없습니다."));

        InformationComment informationComment = InformationComment.builder()
                .information(information)
                .user(user)
                .content(informationCommentRequest.getComment())
                .createdDate(LocalDateTime.now())
                .build();

        InformationComment savedInformationComment = informationCommentRepository.save(informationComment);

        return new InformationCommentResponse(savedInformationComment.getId());
    }

    @Transactional
    public void modifyInformationComment(Long userId, Long informationCommentId, @Valid InformationCommentRequest informationCommentRequest) {
        InformationComment informationComment = informationCommentRepository.findById(informationCommentId)
                .orElseThrow(() -> new InformationCommentNotExistsException("정보에 해당하는 댓글이 없습니다."));

        if (!informationComment.getUser().getId().equals(userId)) {
            throw new CommentNotOwnerException("댓글을 작성한 사용자가 아닙니다.");
        }

        informationComment.updateComment(informationCommentRequest);
    }

    @Transactional
    public void deleteInformationComment(Long userId, Long informationCommentId) {
        InformationComment informationComment = informationCommentRepository.findById(informationCommentId)
                .orElseThrow(() -> new InformationCommentNotExistsException("정보에 해당하는 댓글이 없습니다."));

        if (!informationComment.getUser().getId().equals(userId)) {
            throw new CommentNotOwnerException("댓글을 작성한 사용자가 아닙니다.");
        }

        informationCommentRepository.delete(informationComment);
    }
}
