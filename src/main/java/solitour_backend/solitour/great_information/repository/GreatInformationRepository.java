package solitour_backend.solitour.great_information.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solitour_backend.solitour.great_information.entity.GreatInformation;

public interface GreatInformationRepository extends JpaRepository<GreatInformation, Long> {

    @Query("SELECT COUNT(g) FROM GreatInformation g WHERE g.information.id = :informationId")
    int countByInformationId(Long informationId);

    Optional<GreatInformation> findByInformationIdAndUserId(Long informationId, Long userId);

    void deleteAllByInformationId(Long informationId);

    boolean existsByInformationIdAndUserId(Long informationId, Long userId);
}
