package solitour_backend.solitour.tag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.tag.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {

}
