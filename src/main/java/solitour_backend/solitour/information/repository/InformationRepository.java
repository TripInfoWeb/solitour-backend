package solitour_backend.solitour.information.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.information.entity.Information;


public interface InformationRepository extends JpaRepository<Information, Long>, InformationRepositoryCustom {

}
