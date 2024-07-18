package solitour_backend.solitour.image.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findAllByInformationId(Long informationId);
}
