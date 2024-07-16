package solitour_backend.solitour.great_information.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.great_information.entity.GreatInformation;

public interface GreatInformationRepository extends JpaRepository<GreatInformation, Long> {

  int countByInformationId(Long informationId);
}
