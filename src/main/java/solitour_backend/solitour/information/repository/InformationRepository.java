package solitour_backend.solitour.information.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.user.entity.User;

import java.util.Optional;


public interface InformationRepository extends JpaRepository<Information, Long>, InformationRepositoryCustom {

    @Query("SELECT i FROM Information i WHERE i.user.id = :userId")
    Optional<Information> findByUserId(Long userId);
}
