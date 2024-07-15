package solitour_backend.solitour.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.image.entity.Image;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

  List<Image> findAllByInformationId(Long informationId);
}
