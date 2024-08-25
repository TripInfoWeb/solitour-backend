package solitour_backend.solitour.image.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.image.entity.Image;
import solitour_backend.solitour.image.image_status.ImageStatus;

public interface ImageRepository extends JpaRepository<Image, Long> {

    boolean existsByInformationIdAndImageStatus(Long informationId, ImageStatus imageStatus);

    List<Image> findAllByInformationId(Long informationId);

    boolean existsImageByAddress(String address);

    void deleteByAddress(String address);

    void deleteAllByInformationId(Long informationId);
}
