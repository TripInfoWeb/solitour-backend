package solitour_backend.solitour.gathering.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.book_mark_gathering.entity.QBookMarkGathering;
import solitour_backend.solitour.gathering.dto.request.GatheringPageRequest;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringRankResponse;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.gathering.entity.QGathering;
import solitour_backend.solitour.gathering_applicants.entity.GatheringStatus;
import solitour_backend.solitour.gathering_applicants.entity.QGatheringApplicants;
import solitour_backend.solitour.gathering_category.entity.QGatheringCategory;
import solitour_backend.solitour.gathering_tag.entity.QGatheringTag;
import solitour_backend.solitour.great_gathering.entity.QGreatGathering;
import solitour_backend.solitour.zone_category.entity.QZoneCategory;

public class GatheringRepositoryImpl extends QuerydslRepositorySupport implements GatheringRepositoryCustom {

    public GatheringRepositoryImpl() {
        super(Gathering.class);
    }

    QGathering gathering = QGathering.gathering;
    QZoneCategory zoneCategoryChild = QZoneCategory.zoneCategory;
    QZoneCategory zoneCategoryParent = new QZoneCategory("zoneCategoryParent");
    QBookMarkGathering bookMarkGathering = QBookMarkGathering.bookMarkGathering;
    QGreatGathering greatGathering = QGreatGathering.greatGathering;
    QGatheringCategory category = QGatheringCategory.gatheringCategory;
    QGatheringApplicants gatheringApplicants = QGatheringApplicants.gatheringApplicants;
    QGatheringTag gatheringTag = QGatheringTag.gatheringTag;

    @Override
    public List<GatheringBriefResponse> getGatheringRecommend(Long gatheringId, Long gatheringCategoryId, Long userId) {
        return from(gathering)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(gathering.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .where(gathering.isFinish.eq(Boolean.FALSE)
                        .and(gathering.gatheringCategory.id.eq(gatheringCategoryId))
                        .and(gathering.id.ne(gatheringId))
                        .and(gathering.isDeleted.eq(Boolean.FALSE))
                        .and(gathering.deadline.after(LocalDateTime.now()))
                )
                .groupBy(gathering.id, zoneCategoryChild.id, zoneCategoryParent.id, category.id,
                        gathering.title, gathering.viewCount, gathering.user.name,
                        gathering.scheduleStartDate, gathering.scheduleEndDate,
                        gathering.deadline, gathering.allowedSex,
                        gathering.startAge, gathering.endAge, gathering.personCount)
                .orderBy(gathering.createdAt.desc())
                .select(Projections.constructor(
                        GatheringBriefResponse.class,
                        gathering.id,
                        gathering.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        gathering.viewCount,
                        isGatheringBookmark(userId, gathering.id),
                        countGreatGatheringByGatheringById(gathering.id),
                        gathering.gatheringCategory.name,
                        gathering.user.nickname,
                        gathering.scheduleStartDate,
                        gathering.scheduleEndDate,
                        gathering.deadline,
                        gathering.allowedSex,
                        gathering.startAge,
                        gathering.endAge,
                        gathering.personCount,
                        applicantsCountNowConsent(gathering.id),
                        isUserGreatGathering(userId)
                )).limit(3L).fetch();
    }


    @Override
    public Page<GatheringBriefResponse> getGatheringPageFilterAndOrder(Pageable pageable,
                                                                       GatheringPageRequest gatheringPageRequest,
                                                                       Long userId) {
        BooleanBuilder booleanBuilder = makeWhereSQL(gatheringPageRequest);

        long total = from(gathering)
                .where(booleanBuilder)
                .select(gathering.id).fetchCount();

        List<GatheringBriefResponse> content = from(gathering)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(gathering.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .where(booleanBuilder)
                .groupBy(gathering.id, zoneCategoryChild.id, zoneCategoryParent.id, category.id,
                        gathering.title, gathering.viewCount, gathering.user.name,
                        gathering.scheduleStartDate, gathering.scheduleEndDate,
                        gathering.deadline, gathering.allowedSex,
                        gathering.startAge, gathering.endAge, gathering.personCount)
                .orderBy(getOrderSpecifier(gatheringPageRequest.getSort(), gathering.id))
                .select(Projections.constructor(
                        GatheringBriefResponse.class,
                        gathering.id,
                        gathering.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        gathering.viewCount,
                        isGatheringBookmark(userId, gathering.id),
                        countGreatGatheringByGatheringById(gathering.id),
                        gathering.gatheringCategory.name,
                        gathering.user.nickname,
                        gathering.scheduleStartDate,
                        gathering.scheduleEndDate,
                        gathering.deadline,
                        gathering.allowedSex,
                        gathering.startAge,
                        gathering.endAge,
                        gathering.personCount,
                        applicantsCountNowConsent(gathering.id),
                        isUserGreatGathering(userId)
                ))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<GatheringBriefResponse> getPageGatheringByTag(Pageable pageable,
                                                              GatheringPageRequest gatheringPageRequest, Long userId,
                                                              String decodedTag) {
        BooleanBuilder booleanBuilder = makeWhereSQL(gatheringPageRequest);

        long total = from(gathering)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(gathering.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkGathering)
                .on(bookMarkGathering.gathering.id.eq(gathering.id).and(bookMarkGathering.user.id.eq(userId)))
                .leftJoin(gatheringApplicants).on(gatheringApplicants.gathering.id.eq(gathering.id))
                .leftJoin(gatheringTag)
                .on(gatheringTag.gathering.id.eq(gathering.id).and(gatheringTag.tag.name.eq(decodedTag)))
                .where(booleanBuilder.and(gatheringTag.tag.name.eq(decodedTag)))
                .select(gathering.id.count()).fetchCount();

        List<GatheringBriefResponse> content = from(gathering)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(gathering.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .leftJoin(bookMarkGathering)
                .on(gatheringApplicants.gathering.id.eq(gathering.id).and(gatheringApplicants.gatheringStatus.eq(GatheringStatus.CONSENT)))
                .leftJoin(gatheringApplicants).on(gatheringApplicants.gathering.id.eq(gathering.id).and(gatheringApplicants.gatheringStatus.eq(GatheringStatus.CONSENT)))
                .leftJoin(gatheringTag)
                .on(gatheringTag.gathering.id.eq(gathering.id).and(gatheringTag.tag.name.eq(decodedTag)))
                .where(booleanBuilder.and(gatheringTag.tag.name.eq(decodedTag)))
                .groupBy(gathering.id, zoneCategoryChild.id, zoneCategoryParent.id, category.id)
                .orderBy(getOrderSpecifier(gatheringPageRequest.getSort(), gathering.id))
                .select(Projections.constructor(
                        GatheringBriefResponse.class,
                        gathering.id,
                        gathering.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        gathering.viewCount,
                        bookMarkGathering.user.id.isNotNull(),
                        countGreatGatheringByGatheringById(gathering.id),
                        gathering.gatheringCategory.name,
                        gathering.user.name,
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

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<GatheringRankResponse> getGatheringRankList() {
        return from(gathering)
                .orderBy(countGreatGatheringByGatheringById(gathering.id).desc())
                .groupBy(gathering.id, gathering.title)
                .where(gathering.isFinish.eq(Boolean.FALSE)
                        .and(gathering.isDeleted.eq(Boolean.FALSE))
                        .and(gathering.deadline.after(LocalDateTime.now()))
                )
                .limit(5)
                .select(Projections.constructor(
                        GatheringRankResponse.class,
                        gathering.id,
                        gathering.title))
                .fetch();
    }

    @Override
    public List<GatheringBriefResponse> getGatheringLikeCountFromCreatedIn3(Long userId) {
        return from(gathering)
                .join(zoneCategoryChild).on(zoneCategoryChild.id.eq(gathering.zoneCategory.id))
                .leftJoin(zoneCategoryParent).on(zoneCategoryParent.id.eq(zoneCategoryChild.parentZoneCategory.id))
                .where(gathering.isFinish.eq(Boolean.FALSE)
                        .and(gathering.isDeleted.eq(Boolean.FALSE))
                        .and(gathering.createdAt.after(LocalDateTime.now().minusMonths(3)))
                        .and(gathering.deadline.after(LocalDateTime.now())))
                .groupBy(gathering.id, zoneCategoryChild.id, zoneCategoryParent.id, category.id,
                        gathering.title, gathering.viewCount, gathering.user.name,
                        gathering.scheduleStartDate, gathering.scheduleEndDate,
                        gathering.deadline, gathering.allowedSex,
                        gathering.startAge, gathering.endAge, gathering.personCount)
                .orderBy(countGreatGatheringByGatheringById(gathering.id).desc())
                .select(Projections.constructor(
                        GatheringBriefResponse.class,
                        gathering.id,
                        gathering.title,
                        zoneCategoryParent.name,
                        zoneCategoryChild.name,
                        gathering.viewCount,
                        isGatheringBookmark(userId, gathering.id),
                        countGreatGatheringByGatheringById(gathering.id),
                        gathering.gatheringCategory.name,
                        gathering.user.nickname,
                        gathering.scheduleStartDate,
                        gathering.scheduleEndDate,
                        gathering.deadline,
                        gathering.allowedSex,
                        gathering.startAge,
                        gathering.endAge,
                        gathering.personCount,
                        applicantsCountNowConsent(gathering.id),
                        isUserGreatGathering(userId)
                )).limit(6L).fetch();
    }

    //where 절
    private BooleanBuilder makeWhereSQL(GatheringPageRequest gatheringPageRequest) {
        BooleanBuilder whereClause = new BooleanBuilder();

        whereClause.and(gathering.isDeleted.eq(Boolean.FALSE));
        if (Objects.nonNull(gatheringPageRequest.getCategory())) {
            whereClause.and(gathering.gatheringCategory.id.eq(gatheringPageRequest.getCategory()));
        }

        if (Objects.nonNull(gatheringPageRequest.getLocation())) {
            whereClause.and(gathering.zoneCategory.parentZoneCategory.id.eq(gatheringPageRequest.getLocation()));
        }

        if (Objects.nonNull(gatheringPageRequest.getAllowedSex())) {
            whereClause.and(gathering.allowedSex.eq(gatheringPageRequest.getAllowedSex()));
        }
        int currentYear = LocalDate.now().getYear();

        if (Objects.nonNull(gatheringPageRequest.getStartAge()) && Objects.nonNull(gatheringPageRequest.getEndAge())) {
            int userMinBirthYear = currentYear - gatheringPageRequest.getEndAge() + 1;
            int userMaxBirthYear = currentYear - gatheringPageRequest.getStartAge() + 1;

            whereClause.and(gathering.startAge.goe(userMaxBirthYear)).or(gathering.endAge.loe(userMinBirthYear));
        }

        if (Objects.nonNull(gatheringPageRequest.getStartDate()) && Objects.nonNull(gatheringPageRequest.getEndDate())) {
            whereClause.and(gathering.scheduleStartDate.goe(gatheringPageRequest.getStartDate().atStartOfDay()))
                    .and(gathering.scheduleEndDate.loe(gatheringPageRequest.getEndDate().atTime(LocalTime.MAX)));
        }

        if (Objects.isNull(gatheringPageRequest.getIsExclude())) {
            whereClause.and(gathering.isFinish.eq(Boolean.FALSE));
            whereClause.and(gathering.deadline.after(LocalDateTime.now()));
        }

        if (Objects.nonNull(gatheringPageRequest.getSearch())) {
            String searchKeyword = gatheringPageRequest.getSearch().trim();
            whereClause.and(gathering.title.trim().containsIgnoreCase(searchKeyword));
        }

        return whereClause;
    }

    // 정렬 방식
    private OrderSpecifier<?> getOrderSpecifier(String sort, NumberPath<Long> gatheringId) {
        PathBuilder<Gathering> entityPath = new PathBuilder<>(Gathering.class, "gathering");

        if (Objects.nonNull(sort)) {
            if (LIKE_COUNT_SORT.equalsIgnoreCase(sort)) {
                return countGreatGatheringByGatheringById(gatheringId).desc();
            } else if (VIEW_COUNT_SORT.equalsIgnoreCase(sort)) {
                return entityPath.getNumber("viewCount", Integer.class).desc();
            }
        }

        return entityPath.getDateTime("createdAt", LocalDateTime.class).desc();
    }

    // 좋아요 수 가져오는 식
    private NumberExpression<Integer> countGreatGatheringByGatheringById(NumberPath<Long> gatheringId) {
        QGreatGathering greatGatheringSub = QGreatGathering.greatGathering;
        JPQLQuery<Long> likeCountSubQuery = JPAExpressions
                .select(greatGatheringSub.count())
                .from(greatGatheringSub)
                .where(greatGatheringSub.gathering.id.eq(gatheringId));

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

    private BooleanExpression isGatheringBookmark(Long userId, NumberPath<Long> gatheringId) {
        return new CaseBuilder()
                .when(JPAExpressions.selectOne()
                        .from(bookMarkGathering)
                        .where(bookMarkGathering.gathering.id.eq(gatheringId)
                                .and(bookMarkGathering.user.id.eq(userId)))
                        .exists())
                .then(true)
                .otherwise(false);
    }

    private NumberExpression<Integer> applicantsCountNowConsent(NumberPath<Long> gatheringId) {
        JPQLQuery<Integer> applicantsCountSubQuery = JPAExpressions
                .select(gatheringApplicants.count().intValue())
                .from(gatheringApplicants)
                .where(gatheringApplicants.gathering.id.eq(gatheringId)
                        .and(gatheringApplicants.gatheringStatus.eq(GatheringStatus.CONSENT)));

        return Expressions.numberTemplate(Integer.class, "{0}", applicantsCountSubQuery)
                .coalesce(0);
    }

}
