package solitour_backend.solitour.information.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.book_mark_information.entity.QBookMarkInformation;
import solitour_backend.solitour.category.entity.QCategory;
import solitour_backend.solitour.great_information.entity.QGreatInformation;
import solitour_backend.solitour.image.entity.QImage;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.entity.QInformation;
import solitour_backend.solitour.zone_category.entity.QZoneCategory;

public class InformationRepositoryImpl extends QuerydslRepositorySupport implements InformationRepositoryCustom {

    public InformationRepositoryImpl() {
        super(Information.class);
    }

    QInformation information = QInformation.information;
    QZoneCategory zoneCategoryChild = QZoneCategory.zoneCategory;
    QZoneCategory zoneCategoryParent = new QZoneCategory("zoneCategoryParent");
    QBookMarkInformation bookMarkInformation = QBookMarkInformation.bookMarkInformation;
    QImage image = QImage.image;
    QGreatInformation greatInformation = QGreatInformation.greatInformation;
    QCategory category = QCategory.category;


    @Override
    public Page<InformationBriefResponse> getInformationPageFilterAndOrder(Pageable pageable,
                                                                           InformationPageRequest informationPageRequest,
                                                                           Long userId, Long parentCategoryId) {
        BooleanBuilder whereClause = new BooleanBuilder();

        if (Objects.nonNull(informationPageRequest.getZoneCategoryId())) {
            whereClause.and(
                    information.zoneCategory.parentZoneCategory.id.eq(informationPageRequest.getZoneCategoryId()));
        }

        BooleanBuilder categoryCondition = new BooleanBuilder();

        if (Objects.nonNull(informationPageRequest.getChildCategoryId())) {
            whereClause.and(information.category.id.eq(informationPageRequest.getChildCategoryId()));
        } else {
            categoryCondition.and(category.parentCategory.id.eq(parentCategoryId));
        }

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(informationPageRequest.getSort());
        NumberExpression<Integer> countGreatInformation = countGreatInformationByInformationById();

        long total = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image)
                .on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .join(category).on(category.id.eq(information.category.id).and(categoryCondition))
                .where(whereClause)
                .select(information.count()).fetchCount();

        List<InformationBriefResponse> list = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image)
                .on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .join(category).on(category.id.eq(information.category.id).and(categoryCondition))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(whereClause)
                .groupBy(information.id, zoneCategoryChild.id, zoneCategoryParent.id, image.id)
                .orderBy(orderSpecifier)
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        countGreatInformation,
                        isUserGreatInformation(userId)
                )).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(list, pageable, total);
    }


    @Override
    public List<InformationMainResponse> getInformationLikeCountFromCreatedIn3(Long userId) {
        return from(information)
                .leftJoin(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image)
                .on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .leftJoin(category).on(category.id.eq(information.category.id))
                .where(information.createdDate.after(LocalDateTime.now().minusMonths(3)))
                .groupBy(information.id, information.title, zoneCategoryParent.name, zoneCategoryChild.name,
                        bookMarkInformation.id, image.address)
                .orderBy(greatInformation.information.id.count().desc())
                .select(Projections.constructor(
                        InformationMainResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        category.parentCategory.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        countGreatInformationByInformationById(),
                        isUserGreatInformation(userId)
                )).limit(6).fetch();

    }

    @Override
    public List<InformationBriefResponse> getInformationRecommend(Long informationId, Long childCategoryId,
                                                                  Long userId) {
        return from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.id.eq(childCategoryId).and(information.id.ne(informationId)))
                .groupBy(information.id, zoneCategoryChild.id, zoneCategoryParent.id, image.id)
                .orderBy(information.createdDate.desc())
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        countGreatInformationByInformationById(),
                        isUserGreatInformation(userId)
                ))
                .limit(3L)
                .fetch();
    }

    @Override
    public List<InformationRankResponse> getInformationRank() {
        return from(information)
                .leftJoin(greatInformation)
                .on(greatInformation.information.id.eq(information.id))
                .groupBy(information.id, information.title)
                .orderBy(greatInformation.information.id.count().desc())
                .limit(5)
                .select(Projections.constructor(
                        InformationRankResponse.class,
                        information.id,
                        information.title
                )).fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        if (Objects.nonNull(sort)) {
            if (Objects.equals(LIKE_COUNT_SORT, sort)) {
                return countGreatInformationByInformationById().desc();
            } else if (Objects.equals(VIEW_COUNT_SORT, sort)) {
                return information.viewCount.desc();
            }
        }
        return information.createdDate.desc();
    }

    private NumberExpression<Integer> countGreatInformationByInformationById() {
        QGreatInformation greatInformationSub = QGreatInformation.greatInformation;
        JPQLQuery<Long> likeCountSubQuery = JPAExpressions
                .select(greatInformationSub.count())
                .from(greatInformationSub)
                .where(greatInformationSub.information.id.eq(information.id));

        return Expressions.numberTemplate(Long.class, "{0}", likeCountSubQuery).coalesce(0L).intValue();
    }

    private BooleanExpression isUserGreatInformation(Long userId) {
        return new CaseBuilder()
                .when(JPAExpressions.selectOne()
                        .from(greatInformation)
                        .where(greatInformation.information.id.eq(information.id)
                                .and(greatInformation.information.id.eq(userId)))
                        .exists())
                .then(true)
                .otherwise(false);
    }


}
