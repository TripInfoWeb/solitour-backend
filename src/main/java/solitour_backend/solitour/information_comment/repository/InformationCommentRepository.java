package solitour_backend.solitour.information_comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.information_comment.entity.InformationComment;


public interface InformationCommentRepository extends JpaRepository<InformationComment, Long>, InformationCommentRepositoryCustom {
}

