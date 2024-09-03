package solitour_backend.solitour.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByParentCategoryId(Long parentCategoryId);

    boolean existsByIdAndParentCategoryId(Long id, Long parentCategoryId);
}
