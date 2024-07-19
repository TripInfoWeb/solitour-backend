package solitour_backend.solitour.information.repository;

import com.querydsl.core.types.Projections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.book_mark_information.entity.QBookMarkInformation;
import solitour_backend.solitour.great_information.entity.QGreatInformation;
import solitour_backend.solitour.image.entity.QImage;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.entity.QInformation;
import solitour_backend.solitour.zone_category.entity.QZoneCategory;

import java.util.List;

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


    @Override
    public Page<InformationBriefResponse> getInformationByParentCategory(Pageable pageable, Long categoryId, Long userId) {
        List<InformationBriefResponse> list = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation).on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.parentCategory.id.eq(categoryId))
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

        long total = from(information).where(information.category.id.eq(categoryId)).fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<InformationBriefResponse> getInformationByChildCategory(Pageable pageable, Long categoryId, Long userId) {
        List<InformationBriefResponse> list = from(information)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(information.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkInformation).on(bookMarkInformation.information.id.eq(information.id).and(bookMarkInformation.user.id.eq(userId)))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.category.id.eq(categoryId))
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

        long total = from(information).where(information.category.id.eq(categoryId)).fetchCount();

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