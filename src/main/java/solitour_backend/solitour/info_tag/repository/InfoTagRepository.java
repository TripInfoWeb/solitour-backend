package solitour_backend.solitour.info_tag.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.info_tag.entity.InfoTag;

public interface InfoTagRepository extends JpaRepository<InfoTag, Long> {

    List<InfoTag> findAllByInformationId(Long informationId);

    void deleteAllByInformationId(Long informationId);

}
