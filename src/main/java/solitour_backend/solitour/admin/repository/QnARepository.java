package solitour_backend.solitour.admin.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solitour_backend.solitour.admin.dto.response.QnaListResponseDto;
import solitour_backend.solitour.admin.entity.QnA;

public interface QnARepository extends JpaRepository<QnA, Long> {

    @Query("SELECT new solitour_backend.solitour.admin.dto.response.QnaListResponseDto(q.id, q.title, q.createdAt, q.status, q.updatedAt, q.categoryName, u.id, u.nickname) " +
            "FROM QnA q JOIN User u ON q.userId = u.id WHERE q.userId = :userId ORDER BY q.updatedAt ASC")
    Page<QnA> findByUserId(Long userId, Pageable pageable);

    // 상태에 관계없이 모든 QnA 항목 검색 (updatedAt 기준 오름차순)
    @Query("SELECT new solitour_backend.solitour.admin.dto.response.QnaListResponseDto(q.id, q.title, q.createdAt, q.status, q.updatedAt, q.categoryName, u.id, u.nickname) " +
            "FROM QnA q JOIN User u ON q.userId = u.id ORDER BY q.updatedAt ASC")
    Page<QnaListResponseDto> findAllByOrderByUpdatedAtAsc(Pageable pageable);

    // 상태를 기준으로 QnA 항목 검색 (updatedAt 기준 오름차순)
    @Query("SELECT new solitour_backend.solitour.admin.dto.response.QnaListResponseDto(q.id, q.title, q.createdAt, q.status, q.updatedAt, q.categoryName, u.id, u.nickname) " +
            "FROM QnA q JOIN User u ON q.userId = u.id WHERE q.status = :status ORDER BY q.updatedAt ASC")
    Page<QnaListResponseDto> findByStatusOrderByUpdatedAtAsc(@Param("status") String status, Pageable pageable);

    // 상태와 무관하게 키워드와 유저 닉네임을 기준으로 QnA 항목 검색 (updatedAt 기준 오름차순)
    @Query("SELECT new solitour_backend.solitour.admin.dto.response.QnaListResponseDto(q.id, q.title, q.createdAt, q.status, q.updatedAt, q.categoryName, u.id, u.nickname) " +
            "FROM QnA q JOIN User u ON q.userId = u.id WHERE (q.title LIKE %:keyword% OR q.categoryName LIKE %:keyword% OR u.nickname LIKE %:keyword%) " +
            "ORDER BY q.updatedAt ASC")
    Page<QnaListResponseDto> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 상태와 키워드, 유저 닉네임을 기준으로 QnA 항목 검색 (updatedAt 기준 오름차순)
    @Query("SELECT new solitour_backend.solitour.admin.dto.response.QnaListResponseDto(q.id, q.title, q.createdAt, q.status, q.updatedAt, q.categoryName, u.id, u.nickname) " +
            "FROM QnA q JOIN User u ON q.userId = u.id WHERE q.status = :status AND (q.title LIKE %:keyword% OR q.categoryName LIKE %:keyword% OR u.nickname LIKE %:keyword%) " +
            "ORDER BY q.updatedAt ASC")
    Page<QnaListResponseDto> findByStatusAndKeyword(@Param("status") String status, @Param("keyword") String keyword, Pageable pageable);
}
