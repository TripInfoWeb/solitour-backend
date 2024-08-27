package solitour_backend.solitour.book_mark_gathering.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solitour_backend.solitour.book_mark_gathering.entity.BookMarkGathering;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformation;

public interface BookMarkGatheringRepository extends JpaRepository<BookMarkGathering, Long> {

    @Query("SELECT b FROM BookMarkGathering b JOIN FETCH b.user u JOIN FETCH b.gathering i WHERE u.id = :userId")
    List<BookMarkGathering> findByUserId(Long userId);

    Optional<BookMarkGathering> findByGatheringIdAndUserId(Long gatheringId, Long userId);
}
