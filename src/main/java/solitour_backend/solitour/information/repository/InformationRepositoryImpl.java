package solitour_backend.solitour.information.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.book_mark_information.entity.QBookMarkInformation;
import solitour_backend.solitour.category.entity.QCategory;
import solitour_backend.solitour.great_information.entity.QGreatInformation;
import solitour_backend.solitour.image.entity.QImage;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.info_tag.entity.QInfoTag;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.entity.QInformation;
import solitour_backend.solitour.zone_category.entity.QZoneCategory;

@Slf4j
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
    QCategory categoryParent = new QCategory("categoryParent");
    QInfoTag infoTag = QInfoTag.infoTag;

    public Page<InformationBriefResponse> getPageInformationFilterAndOrder(Pageable pageable, InformationPageRequest informationPageRequest, Long userId, Long parentCategoryId) {
        BooleanBuilder whereClause = new BooleanBuilder();
        if (Objects.nonNull(informationPageRequest.getZoneCategoryId())) {
            whereClause.and(
                    zoneCategoryParent.id.eq(informationPageRequest.getZoneCategoryId())
            );
        }

        if (Objects.nonNull(informationPageRequest.getChildCategoryId())) {
            whereClause.and(category.id.eq(informationPageRequest.getChildCategoryId()));
        } else {
            whereClause.and(categoryParent.id.eq(parentCategoryId));
        }
        if (Objects.nonNull(informationPageRequest.getSearch())) {
            String searchKeyword = informationPageRequest.getSearch().trim().replace(" ", "");
            whereClause.and(information.title.trim().containsIgnoreCase(searchKeyword));
        }

        long total = from(information)
                .leftJoin(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryChild.parentZoneCategory.id.eq(zoneCategoryParent.id))
                .leftJoin(category).on(category.id.eq(information.category.id))
                .leftJoin(categoryParent).on(categoryParent.id.eq(category.parentCategory.id))
                .join(image).on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .where(whereClause)
                .distinct()
                .fetchCount();

        List<InformationBriefResponse> list = from(information)
                .leftJoin(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryChild.parentZoneCategory.id.eq(zoneCategoryParent.id))
                .leftJoin(category).on(category.id.eq(information.category.id))
                .leftJoin(categoryParent).on(categoryParent.id.eq(category.parentCategory.id))
                .leftJoin(image).on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(bookMarkInformation).on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(whereClause)
                .groupBy(information.id, information.createdDate, information.viewCount, zoneCategoryChild.name, bookMarkInformation.id, image.address)
                .orderBy(getOrderSpecifiers(informationPageRequest.getSort()))
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.category.name,
                        information.viewCount,
                        isBookMarkBooleanExpression(bookMarkInformation),
                        image.address,
                        countGreatInformation(greatInformation),
                        isGreatBooleanExpression(userId, greatInformation)
                )).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        log.info("정보 페이지 네이션 총 갯수 : " + total + "\n");
        log.info("정보 들 : ");
        log.info(list.toString());
        return new PageImpl<>(list, pageable, total);
    }

    private OrderSpecifier<?> getOrderSpecifiers(String sort) {
        if (Objects.nonNull(sort)) {
            if (Objects.equals(LIKE_COUNT_SORT, sort)) {
                return countGreatInformation(greatInformation).desc();
            } else if (Objects.equals(VIEW_COUNT_SORT, sort)) {
                return information.viewCount.desc();
            }
        }
        return information.createdDate.desc();
    }

    private BooleanExpression isBookMarkBooleanExpression(QBookMarkInformation bookMarkInformation) {
        return new CaseBuilder()
                .when(bookMarkInformation.id.isNotNull())
                .then(true)
                .otherwise(false);
    }

    private BooleanExpression isGreatBooleanExpression(Long userId, QGreatInformation greatInformation) {
        return greatInformation.user.id.eq(userId).count().gt(0);
    }

    private NumberExpression<Integer> countGreatInformation(QGreatInformation greatInformation) {
        return greatInformation.id.count().intValue();
    }


    @Override
    public List<InformationMainResponse> getInformationLikeCountFromCreatedIn3(Long userId) {
        return from(information)
                .leftJoin(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryChild.parentZoneCategory.id.eq(zoneCategoryParent.id))
                .leftJoin(category).on(category.id.eq(information.category.id))
                .leftJoin(categoryParent).on(categoryParent.id.eq(category.parentCategory.id))
                .leftJoin(image).on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(bookMarkInformation).on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.createdDate.after(LocalDateTime.now().minusMonths(3)))
                .groupBy(information.id, information.createdDate, information.viewCount, zoneCategoryChild.name, bookMarkInformation.id, image.address)
                .orderBy(countGreatInformation(greatInformation).desc())
                .select(Projections.constructor(
                        InformationMainResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.category.name,
                        information.viewCount,
                        isBookMarkBooleanExpression(bookMarkInformation),
                        image.address,
                        countGreatInformation(greatInformation),
                        isGreatBooleanExpression(userId, greatInformation)
                )).limit(6).fetch();
    }

    @Override
    public List<InformationBriefResponse> getInformationRecommend(Long informationId, Long childCategoryId,
                                                                  Long userId) {
        return from(information)
                .leftJoin(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryChild.parentZoneCategory.id.eq(zoneCategoryParent.id))
                .leftJoin(category).on(category.id.eq(information.category.id))
                .leftJoin(categoryParent).on(categoryParent.id.eq(category.parentCategory.id))
                .leftJoin(image).on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(bookMarkInformation).on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.id.eq(childCategoryId).and(information.id.ne(informationId)))
                .groupBy(information.id, information.createdDate, information.viewCount, zoneCategoryChild.name, bookMarkInformation.id, image.address)
                .orderBy(information.createdDate.desc())
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.category.name,
                        information.viewCount,
                        isBookMarkBooleanExpression(bookMarkInformation),
                        image.address,
                        countGreatInformation(greatInformation),
                        isGreatBooleanExpression(userId, greatInformation)
                ))
                .limit(3L)
                .fetch();
    }

    @Override
    public Page<InformationBriefResponse> getInformationPageByTag(Pageable pageable, Long userId, Long
            parentCategoryId,
                                                                  InformationPageRequest informationPageRequest,
                                                                  String decodedTag) {
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


        long total = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image)
                .on(image.information.id.eq(information.id).and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .join(category).on(category.id.eq(information.category.id).and(categoryCondition))
                .leftJoin(infoTag)
                .on(infoTag.information.id.eq(information.id))
                .where(whereClause.and(infoTag.information.id.eq(information.id).and(infoTag.tag.name.eq(decodedTag))))
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
                .leftJoin(infoTag)
                .on(infoTag.information.id.eq(information.id))
                .where(whereClause)
                .groupBy(information.id, zoneCategoryChild.id, zoneCategoryParent.id, image.id, infoTag.id)
                .orderBy(getOrderSpecifier(informationPageRequest.getSort(), information.id))
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.category.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        countGreatInformationByInformationByIdSubQuery(information.id),
                        isUserGreatInformationSubQuery(userId)
                )).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<InformationRankResponse> getInformationRank() {
        return from(information)
                .leftJoin(greatInformation)
                .on(greatInformation.information.id.eq(information.id))
                .groupBy(information.id, information.title)
                .orderBy(countGreatInformation(greatInformation).desc())
                .limit(5)
                .select(Projections.constructor(
                        InformationRankResponse.class,
                        information.id,
                        information.title
                )).fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(String sort, NumberPath<Long> informationId) {
        if (Objects.nonNull(sort)) {
            if (Objects.equals(LIKE_COUNT_SORT, sort)) {
                return countGreatInformationByInformationByIdSubQuery(informationId).desc();
            } else if (Objects.equals(VIEW_COUNT_SORT, sort)) {
                return information.viewCount.desc();
            }
        }
        return information.createdDate.desc();
    }

    private NumberExpression<Integer> countGreatInformationByInformationByIdSubQuery(NumberPath<Long> informationId) {
        QGreatInformation greatInformationSub = QGreatInformation.greatInformation;
        JPQLQuery<Long> likeCountSubQuery = JPAExpressions
                .select(greatInformationSub.count())
                .from(greatInformationSub)
                .where(greatInformationSub.information.id.eq(informationId));

        return Expressions.numberTemplate(Long.class, "{0}", likeCountSubQuery)
                .coalesce(0L)
                .intValue();
    }

    private BooleanExpression isUserGreatInformationSubQuery(Long userId) {
        return new CaseBuilder()
                .when(JPAExpressions.selectOne()
                        .from(greatInformation)
                        .where(greatInformation.information.id.eq(information.id)
                                .and(greatInformation.user.id.eq(userId)))
                        .exists())
                .then(true)
                .otherwise(false);
    }


}
