package solitour_backend.solitour.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.book_mark_gathering.entity.QBookMarkGathering;
import solitour_backend.solitour.book_mark_information.entity.QBookMarkInformation;
import solitour_backend.solitour.gathering.dto.response.GatheringApplicantResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.gathering.entity.QGathering;
import solitour_backend.solitour.gathering_applicants.entity.GatheringStatus;
import solitour_backend.solitour.gathering_applicants.entity.QGatheringApplicants;
import solitour_backend.solitour.gathering_category.entity.QGatheringCategory;
import solitour_backend.solitour.great_gathering.entity.QGreatGathering;
import solitour_backend.solitour.great_information.entity.QGreatInformation;
import solitour_backend.solitour.image.entity.QImage;
import solitour_backend.solitour.image.image_status.ImageStatus;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.entity.QInformation;
import solitour_backend.solitour.place.entity.QPlace;
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
    QGathering gathering = QGathering.gathering;
    QGatheringCategory gatheringCategory = QGatheringCategory.gatheringCategory;
    QPlace place = QPlace.place;
    QBookMarkGathering bookMarkGathering = QBookMarkGathering.bookMarkGathering;
    QGatheringApplicants gatheringApplicants = QGatheringApplicants.gatheringApplicants;
    QGreatGathering greatGathering = QGreatGathering.greatGathering;

    @Override
    public Page<InformationBriefResponse> retrieveInformationOwner(Pageable pageable, Long userId) {
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
    public Page<InformationBriefResponse> retrieveInformationBookmark(Pageable pageable, Long userId) {
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

    @Override
    public Page<GatheringBriefResponse> retrieveGatheringHost(Pageable pageable, Long userId) {
        NumberExpression<Integer> likeCount = countGreatGatheringByGatheringById();
        BooleanExpression isBookMark = isGatheringBookmark(userId);

        JPQLQuery<Gathering> query = from(gathering)
                .leftJoin(zoneCategoryParent)
                .on(zoneCategoryParent.id.eq(gathering.zoneCategory.parentZoneCategory.id))
                .leftJoin(gatheringCategory)
                .on(gatheringCategory.id.eq(gathering.gatheringCategory.id))
                .leftJoin(gatheringApplicants)
                .on(gatheringApplicants.gathering.id.eq(gathering.id))
                .orderBy(gathering.createdAt.desc())
                .where(gathering.user.id.eq(userId));

        List<GatheringBriefResponse> list = query
                .groupBy(gathering.id, gathering.title, zoneCategoryParent.name, zoneCategoryChild.name,
                        gathering.viewCount,
                        gatheringCategory.name, gathering.user.nickname, gathering.scheduleStartDate,
                        gathering.scheduleEndDate, gathering.deadline, gathering.allowedSex, gathering.startAge,
                        gathering.endAge, gathering.personCount)
                .select(Projections.constructor(
                        GatheringBriefResponse.class,
                        gathering.id,
                        gathering.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        gathering.viewCount,
                        isBookMark,
                        likeCount,
                        gatheringCategory.name,
                        gathering.user.nickname,
                        gathering.scheduleStartDate,
                        gathering.scheduleEndDate,
                        gathering.deadline,
                        gathering.allowedSex,
                        gathering.startAge,
                        gathering.endAge,
                        gathering.personCount,
                        gatheringApplicants.count().coalesce(0L).intValue(),
                        isUserGreatGathering(userId)
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<GatheringBriefResponse> retrieveGatheringBookmark(Pageable pageable, Long userId) {
        NumberExpression<Integer> likeCount = countGreatGatheringByGatheringById();
        BooleanExpression isBookMark = isGatheringBookmark(userId);

        JPQLQuery<Gathering> query = from(gathering)
                .leftJoin(zoneCategoryParent)
                .on(zoneCategoryParent.id.eq(gathering.zoneCategory.parentZoneCategory.id))
                .leftJoin(gatheringCategory)
                .on(gatheringCategory.id.eq(gathering.gatheringCategory.id))
                .leftJoin(gatheringApplicants)
                .on(gatheringApplicants.gathering.id.eq(gathering.id))
                .leftJoin(bookMarkGathering)
                .on(bookMarkGathering.gathering.id.eq(gathering.id))
                .orderBy(gathering.createdAt.desc())
                .where(bookMarkGathering.user.id.eq(userId));

        List<GatheringBriefResponse> list = query
                .groupBy(gathering.id, gathering.title, zoneCategoryParent.name, zoneCategoryChild.name,
                        gathering.viewCount,
                        gatheringCategory.name, gathering.user.nickname, gathering.scheduleStartDate,
                        gathering.scheduleEndDate, gathering.deadline, gathering.allowedSex, gathering.startAge,
                        gathering.endAge, gathering.personCount)
                .select(Projections.constructor(
                        GatheringBriefResponse.class,
                        gathering.id,
                        gathering.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        gathering.viewCount,
                        isBookMark,
                        likeCount,
                        gatheringCategory.name,
                        gathering.user.nickname,
                        gathering.scheduleStartDate,
                        gathering.scheduleEndDate,
                        gathering.deadline,
                        gathering.allowedSex,
                        gathering.startAge,
                        gathering.endAge,
                        gathering.personCount,
                        gatheringApplicants.count().coalesce(0L).intValue(),
                        isUserGreatGathering(userId)
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Page<GatheringApplicantResponse> retrieveGatheringApplicant(Pageable pageable, Long userId) {
        NumberExpression<Integer> likeCount = countGreatGatheringByGatheringById();
        BooleanExpression isBookMark = isGatheringBookmark(userId);
        StringExpression gatheringStatus = getGatheringStatus();

        JPQLQuery<Gathering> query = from(gathering)
                .leftJoin(zoneCategoryParent)
                .on(zoneCategoryParent.id.eq(gathering.zoneCategory.parentZoneCategory.id))
                .leftJoin(gatheringCategory)
                .on(gatheringCategory.id.eq(gathering.gatheringCategory.id))
                .leftJoin(gatheringApplicants)
                .on(gatheringApplicants.gathering.id.eq(gathering.id))
                .leftJoin(gatheringApplicants)
                .on(gatheringApplicants.gathering.id.eq(gathering.id))
                .orderBy(gathering.createdAt.desc())
                .where(gatheringApplicants.user.id.eq(userId));

        List<GatheringApplicantResponse> list = query
                .groupBy(gathering.id, gathering.title, zoneCategoryParent.name, zoneCategoryChild.name,
                        gathering.viewCount,
                        gatheringCategory.name, gathering.user.nickname, gathering.scheduleStartDate,
                        gathering.scheduleEndDate, gathering.deadline, gathering.allowedSex, gathering.startAge,
                        gathering.endAge, gathering.personCount, gatheringApplicants.gatheringStatus)
                .select(Projections.constructor(
                        GatheringApplicantResponse.class,
                        gathering.id,
                        gathering.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        gathering.viewCount,
                        isBookMark,
                        likeCount,
                        gatheringCategory.name,
                        gathering.user.nickname,
                        gathering.scheduleStartDate,
                        gathering.scheduleEndDate,
                        gathering.deadline,
                        gathering.allowedSex,
                        gathering.startAge,
                        gathering.endAge,
                        gathering.personCount,
                        gatheringApplicants.count().coalesce(0L).intValue(),
                        isUserGreatGathering(userId),
                        gatheringStatus
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        long total = query.fetchCount();

        return new PageImpl<>(list, pageable, total);
    }

    private StringExpression getGatheringStatus() {
        QGatheringApplicants gatheringApplicants = QGatheringApplicants.gatheringApplicants;
        return new CaseBuilder()
                .when(gatheringApplicants.gatheringStatus.eq(GatheringStatus.WAIT))
                .then("대기중")
                .when(gatheringApplicants.gatheringStatus.eq(GatheringStatus.CONSENT))
                .then("승인됨")
                .otherwise("거절됨");
    }

    private NumberExpression<Integer> countGreatGatheringByGatheringById() {
        QGreatGathering greatGatheringSub = QGreatGathering.greatGathering;
        JPQLQuery<Long> likeCountSubQuery = JPAExpressions
                .select(greatGatheringSub.count())
                .from(greatGatheringSub)
                .where(greatGatheringSub.gathering.id.eq(gathering.id));

        return Expressions.numberTemplate(Long.class, "{0}", likeCountSubQuery)
                .coalesce(0L)
                .intValue();
    }

    private BooleanExpression isUserGreatGathering(Long userId) {
        return new CaseBuilder()
                .when(JPAExpressions.selectOne()
                        .from(greatGathering)
                        .where(greatGathering.gathering.id.eq(gathering.id)
                                .and(greatGathering.user.id.eq(userId)))
                        .exists())
                .then(true)
                .otherwise(false);
    }

    private BooleanExpression isGatheringBookmark(Long userId) {
        return new CaseBuilder()
                .when(JPAExpressions.selectOne()
                        .from(bookMarkGathering)
                        .where(bookMarkGathering.gathering.id.eq(gathering.id)
                                .and(bookMarkGathering.user.id.eq(userId)))
                        .exists())
                .then(true)
                .otherwise(false);
    }


}
