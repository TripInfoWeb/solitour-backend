package solitour_backend.solitour.book_mark_information.entity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookMarkInformationRepository extends JpaRepository<BookMarkInformation, Long> {

    @Query("SELECT b FROM BookMarkInformation b JOIN FETCH b.user u JOIN FETCH b.information i WHERE u.id = :userId")
    List<BookMarkInformation> findByUserId(Long userId);

    Optional<BookMarkInformation> findByInformationIdAndUserId(Long infoId, Long userId);

    void deleteAllByInformationId(Long informationId);
}
