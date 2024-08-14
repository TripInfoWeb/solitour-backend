package solitour_backend.solitour.information.repository;

import com.querydsl.core.types.Projections;
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
import solitour_backend.solitour.info_tag.entity.QInfoTag;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.entity.QInformation;
import solitour_backend.solitour.tag.entity.QTag;
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
    QInfoTag infoTag = QInfoTag.infoTag;


    @Override
    public Page<InformationBriefResponse> getInformationByParentCategoryFilterZoneCategory(Pageable pageable,
                                                                                           Long parentCategoryId,
                                                                                           Long userId,
                                                                                           Long zoneCategoryId) {
        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.parentCategory.id.eq(parentCategoryId));

        if (Objects.nonNull(zoneCategoryId)) {
            query = query.where(information.zoneCategory.parentZoneCategory.id.eq(zoneCategoryId));
        }

        List<InformationBriefResponse> list = query
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
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(parentCategoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<InformationBriefResponse> getInformationByChildCategoryFilterZoneCategory(Pageable pageable,
                                                                                          Long childCategoryId,
                                                                                          Long userId,
                                                                                          Long zoneCategoryId) {
        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.id.eq(childCategoryId));

        if (Objects.nonNull(zoneCategoryId)) {
            query = query.where(information.zoneCategory.parentZoneCategory.id.eq(zoneCategoryId));
        }

        List<InformationBriefResponse> list = query
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
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(childCategoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
    }


    @Override
    public Page<InformationBriefResponse> getInformationByParentCategoryFilterZoneCategoryLikeCount(Pageable pageable,
                                                                                                    Long categoryId,
                                                                                                    Long userId,
                                                                                                    Long zoneCategoryId) {
        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.parentCategory.id.eq(categoryId));

        if (Objects.nonNull(zoneCategoryId)) {
            query = query.where(information.zoneCategory.parentZoneCategory.id.eq(zoneCategoryId));
        }

        List<InformationBriefResponse> list = query.groupBy(information.id, zoneCategoryChild.id, zoneCategoryParent.id,
                        image.id)
                .orderBy(greatInformation.information.count().desc())
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(categoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<InformationBriefResponse> getInformationByChildCategoryFilterZoneCategoryLikeCount(Pageable pageable,
                                                                                                   Long categoryId,
                                                                                                   Long userId,
                                                                                                   Long zoneCategoryId) {
        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.id.eq(categoryId));

        if (Objects.nonNull(zoneCategoryId)) {
            query = query.where(information.zoneCategory.parentZoneCategory.id.eq(zoneCategoryId));
        }

        List<InformationBriefResponse> list = query
                .groupBy(information.id, zoneCategoryChild.id, zoneCategoryParent.id, image.id)
                .orderBy(greatInformation.information.count().desc())
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(categoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<InformationBriefResponse> getInformationByParentCategoryFilterZoneCategoryViewCount(Pageable pageable,
                                                                                                    Long categoryId,
                                                                                                    Long userId,
                                                                                                    Long zoneCategoryId) {
        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.parentCategory.id.eq(categoryId));

        if (Objects.nonNull(zoneCategoryId)) {
            query = query.where(information.zoneCategory.parentZoneCategory.id.eq(zoneCategoryId));
        }

        List<InformationBriefResponse> list = query
                .groupBy(information.id, zoneCategoryChild.id, zoneCategoryParent.id, image.id)
                .orderBy(information.viewCount.desc())
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(categoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<InformationBriefResponse> getInformationByChildCategoryFilterZoneCategoryViewCount(Pageable pageable,
                                                                                                   Long categoryId,
                                                                                                   Long userId,
                                                                                                   Long zoneCategoryId) {
        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.id.eq(categoryId));

        if (Objects.nonNull(zoneCategoryId)) {
            query = query.where(information.zoneCategory.parentZoneCategory.id.eq(zoneCategoryId));
        }

        List<InformationBriefResponse> list = query
                .groupBy(information.id, zoneCategoryChild.id, zoneCategoryParent.id, image.id)
                .orderBy(information.viewCount.desc())
                .select(Projections.constructor(
                        InformationBriefResponse.class,
                        information.id,
                        information.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        information.viewCount,
                        bookMarkInformation.user.id.isNotNull(),
                        image.address,
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(categoryId)).fetchCount();

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
                        greatInformation.information.count().coalesce(0L).intValue()
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
                        greatInformation.information.count().coalesce(0L).intValue()
                )).limit(3L).fetch();
    }

    @Override
    public Page<InformationBriefResponse> getInformationByParentCategoryFilterTag(Pageable pageable,
                                                                                  Long parentCategoryId,
                                                                                  Long userId, String tagName) {

        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(infoTag)
                .on(infoTag.tag.name.eq(tagName))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.parentCategory.id.eq(parentCategoryId).and(infoTag.information.id.eq(information.id)));

        List<InformationBriefResponse> list = query
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
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(parentCategoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<InformationBriefResponse> getInformationByChildCategoryFilterTag(Pageable pageable,
                                                                                 Long childCategoryId,
                                                                                 Long userId, String tagName) {
        JPQLQuery<Information> query = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(infoTag)
                .on(infoTag.tag.name.eq(tagName))
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.id.eq(childCategoryId).and(infoTag.information.id.eq(information.id)));

        List<InformationBriefResponse> list = query
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
                        greatInformation.information.count().coalesce(0L).intValue()
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = from(information).where(information.category.id.eq(childCategoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
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


}
