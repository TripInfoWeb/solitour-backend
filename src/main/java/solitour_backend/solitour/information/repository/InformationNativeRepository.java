package solitour_backend.solitour.information.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import solitour_backend.solitour.information.entity.Information;

public interface InformationNativeRepository extends JpaRepository<Information, Long> {
    @Query(value = "SELECT COUNT(*) FROM information WHERE MATCH(title) AGAINST (?1 IN BOOLEAN MODE)", nativeQuery = true)
    int countByFullTextSearch(String keyword);

    @Query(value = """
        SELECT i.information_id, i.title, zc_parent.zone_category_name as parentZone, 
               zc.zone_category_name as childZone, c.category_name, i.view_count, 
               IF(b.book_mark_information_id IS NOT NULL, TRUE, FALSE) AS isBookMarked,
               img.image_address,
               (SELECT COUNT(*) FROM great_information g WHERE g.information_id = i.information_id) as likeCount,
               (SELECT COUNT(*) > 0 FROM great_information g WHERE g.information_id = i.information_id AND g.user_id = :userId) as isLiked
        FROM information i
        LEFT JOIN zone_category zc ON zc.zone_category_id = i.zone_category_id
        LEFT JOIN zone_category zc_parent ON zc_parent.zone_category_id = zc.parent_zone_category_id
        LEFT JOIN category c ON c.category_id = i.category_id
        LEFT JOIN category c_parent ON c_parent.category_id = c.parent_category_id
        LEFT JOIN image img ON img.information_id = i.information_id AND img.image_status_id = 'THUMBNAIL'
        LEFT JOIN book_mark_information b ON b.information_id = i.information_id AND b.user_id = :userId
        WHERE c_parent.category_id = :parentCategoryId
        AND MATCH(i.title) AGAINST (:searchKeyword IN BOOLEAN MODE)
        ORDER BY i.created_date DESC
        LIMIT :offset, :limit
        """, nativeQuery = true)
    List<Object[]> searchInformationListNative(
            @Param("searchKeyword") String searchKeyword,
            @Param("userId") Long userId,
            @Param("parentCategoryId") Long parentCategoryId,
            @Param("offset") int offset,
            @Param("limit") int limit
    );
}
