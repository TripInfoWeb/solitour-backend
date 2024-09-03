package solitour_backend.solitour.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import solitour_backend.solitour.admin.entity.Notice;

import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAllByIsDeletedFalse(Pageable pageable);

    Optional<Notice> findByIdAndIsDeletedFalse(Long id);
}
