package solitour_backend.solitour.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.category.entity.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  List<Category> findAllByParentCategoryId(Long parentCategoryId);
}
