package solitour_backend.solitour.information.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class InformationNativeRepositoryImpl implements InformationNativeRepository{

    private final EntityManager em;

    @Override
    public Page<InformationBriefResponse> searchByFullTextIndex(Pageable pageable, InformationPageRequest request, Long userId, Long parentCategoryId) {
        String keyword = request.getSearch().trim();

        String orderByClause = switch (Objects.toString(request.getSort(), "")) {
            case "likes" -> "ORDER BY like_count DESC";
            case "views" -> "ORDER BY i.information_view_count DESC";
            default -> "ORDER BY i.information_created_date DESC";
        };

        String countSql = """
                SELECT COUNT(*)
               FROM information i
               JOIN category c ON c.category_id = i.category_id
               JOIN category cp ON cp.category_id = c.parent_category_id
               WHERE cp.category_id = :parentCategoryId
               AND MATCH(i.information_title) AGAINST (:keyword IN BOOLEAN MODE)
               """;

        Query countQuery = em.createNativeQuery(countSql)
                .setParameter("parentCategoryId", parentCategoryId)
                .setParameter("keyword", keyword);
        Long total = ((Number) countQuery.getSingleResult()).longValue();

        String querySql = """
                 SELECT
                   i.information_id                 AS information_id,
                   i.information_title              AS title,
                   zcp.zone_category_name           AS parent_zone_name,
                   zc.zone_category_name            AS zone_name,
                   c.category_name                  AS category_name,
                   i.information_view_count         AS view_count,
                   EXISTS (
                       SELECT 1
                       FROM book_mark_information b
                       WHERE b.information_id = i.information_id AND b.user_id = :userId
                   )                                AS is_bookmarked,
                   img.image_address                AS image_address,
                   (
                       SELECT COUNT(*)
                       FROM great_information g
                       WHERE g.information_id = i.information_id
                   )                                AS like_count,
                   EXISTS (
                       SELECT 1
                       FROM great_information g
                       WHERE g.information_id = i.information_id AND g.user_id = :userId
                   )                                AS is_liked
               FROM information i
               JOIN category c ON c.category_id = i.category_id
               JOIN category cp ON cp.category_id = c.parent_category_id
               LEFT JOIN zone_category zc ON zc.zone_category_id = i.zone_category_id
               LEFT JOIN zone_category zcp ON zcp.zone_category_id = zc.parent_zone_category_id
               LEFT JOIN image img ON img.information_id = i.information_id AND img.image_status_id = 'THUMBNAIL'
               WHERE cp.category_id = :parentCategoryId
                 AND MATCH(i.information_title) AGAINST (:keyword IN BOOLEAN MODE)
               ORDER BY i.information_created_date DESC
               LIMIT :limit OFFSET :offset
            """.formatted(orderByClause);

        Query query = em.createNativeQuery(querySql, "InformationBriefMapping");
        query.setParameter("parentCategoryId", parentCategoryId);
        query.setParameter("keyword", keyword);
        query.setParameter("userId", userId);
        query.setParameter("limit", pageable.getPageSize());
        query.setParameter("offset", pageable.getOffset());

        @SuppressWarnings("unchecked")
        List<InformationBriefResponse> resultList = query.getResultList();

        return new PageImpl<>(resultList, pageable, total);
    }
}
