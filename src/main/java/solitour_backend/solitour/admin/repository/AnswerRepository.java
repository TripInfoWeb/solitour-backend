package solitour_backend.solitour.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.admin.entity.QnAMessage;

public interface AnswerRepository extends JpaRepository<QnAMessage, Long> {
}
