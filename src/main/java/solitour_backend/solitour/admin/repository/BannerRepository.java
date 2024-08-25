package solitour_backend.solitour.admin.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.admin.entity.Banner;

public interface BannerRepository extends JpaRepository<Banner, Long> {


    List<Banner> findAllByOrderById();
}
