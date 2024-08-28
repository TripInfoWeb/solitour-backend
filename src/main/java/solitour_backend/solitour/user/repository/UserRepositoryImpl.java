package solitour_backend.solitour.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.book_mark_information.entity.QBookMarkInformation;
import solitour_backend.solitour.great_information.entity.QGreatInformation;
import solitour_backend.solitour.image.entity.QImage;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.entity.QInformation;
import solitour_backend.solitour.user.entity.QUser;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.zone_category.entity.QZoneCategory;

public class UserRepositoryImpl extends QuerydslRepositorySupport implements UserRepositoryCustom {

    public UserRepositoryImpl() {
        super(User.class);
    }

    QInformation information = QInformation.information;
    QZoneCategory zoneCategoryChild = QZoneCategory.zoneCategory;
    QZoneCategory zoneCategoryParent = new QZoneCategory("zoneCategoryParent");
    QBookMarkInformation bookMarkInformation = QBookMarkInformation.bookMarkInformation;
    QImage image = QImage.image;
    QGreatInformation greatInformation = QGreatInformation.greatInformation;
    QUser user = QUser.user;


    @Override
    public Page<InformationBriefResponse> getInformationByUserId(Pageable pageable, Long userId) {
        JPQLQuery<Information> query = from(information)
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id))
                .leftJoin(zoneCategoryParent)
                .on(zoneCategoryParent.id.eq(information.zoneCategory.parentZoneCategory.id))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.user.id.eq(userId));

        List<InformationBriefResponse> list = query
                .groupBy(information.id, information.title, zoneCategoryParent.name, zoneCategoryChild.name,
                        information.viewCount, bookMarkInformation.user.id, image.address)
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

        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<InformationBriefResponse> getInformationByUserBookMark(Pageable pageable, Long userId) {
        JPQLQuery<Information> query = from(information)
                .leftJoin(bookMarkInformation)
                .on(bookMarkInformation.information.id.eq(information.id))
                .leftJoin(zoneCategoryParent)
                .on(zoneCategoryParent.id.eq(information.zoneCategory.parentZoneCategory.id))
                .leftJoin(image).on(image.information.id.eq(information.id)
                        .and(image.imageStatus.eq(ImageStatus.THUMBNAIL)))
                .leftJoin(greatInformation).on(greatInformation.information.id.eq(information.id))
                .where(information.user.id.eq(userId).and(bookMarkInformation.user.id.eq(userId)));

        List<InformationBriefResponse> list = query
                .groupBy(information.id, information.title, zoneCategoryParent.name, zoneCategoryChild.name,
                        information.viewCount, bookMarkInformation.user.id, image.address)
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

        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

}
