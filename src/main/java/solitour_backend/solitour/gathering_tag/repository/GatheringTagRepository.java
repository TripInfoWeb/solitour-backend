package solitour_backend.solitour.gathering_tag.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.gathering_tag.entity.GatheringTag;

public interface GatheringTagRepository extends JpaRepository<GatheringTag, Long> {
    List<GatheringTag> findAllByGathering_Id(Long gatheringId);

    void deleteAllByGathering_Id(Long gatheringId);
}
