package solitour_backend.solitour.info_tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.info_tag.entity.InfoTag;

import java.util.List;

public interface InfoTagRepository extends JpaRepository<InfoTag, Long> {
    List<InfoTag> findAllByInformationId(Long informationId);
}
