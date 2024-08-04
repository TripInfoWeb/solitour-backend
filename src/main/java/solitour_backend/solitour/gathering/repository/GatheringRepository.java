package solitour_backend.solitour.gathering.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.gathering.entity.Gathering;

public interface GatheringRepository extends JpaRepository<Gathering, Long> {
}
