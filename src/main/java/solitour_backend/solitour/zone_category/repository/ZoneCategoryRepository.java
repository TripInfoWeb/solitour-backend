package solitour_backend.solitour.zone_category.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.zone_category.entity.ZoneCategory;

public interface ZoneCategoryRepository extends JpaRepository<ZoneCategory, Long> {

    Optional<ZoneCategory> findByName(String name);

    Optional<ZoneCategory> findByParentZoneCategoryIdAndName(Long parentZoneCategoryId, String name);
}
