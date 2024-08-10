package solitour_backend.solitour.great_gathering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solitour_backend.solitour.great_gathering.entity.GreatGathering;

public interface GreatGatheringRepository extends JpaRepository<GreatGathering, Long> {

    @Query("SELECT COUNT(g) FROM GreatGathering  g WHERE g.gathering.id = :gatheringId")
    int countByGatheringId(Long gatheringId);
}
