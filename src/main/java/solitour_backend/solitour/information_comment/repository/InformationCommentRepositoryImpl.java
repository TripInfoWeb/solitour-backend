package solitour_backend.solitour.information_comment.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
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
import solitour_backend.solitour.information.repository.InformationRepositoryCustom;
import solitour_backend.solitour.information_comment.dto.request.InformationCommentPageRequest;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentListResponse;
import solitour_backend.solitour.information_comment.entity.InformationComment;
import solitour_backend.solitour.information_comment.entity.QInformationComment;
import solitour_backend.solitour.user.entity.QUser;
import solitour_backend.solitour.zone_category.entity.QZoneCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
public class InformationCommentRepositoryImpl extends QuerydslRepositorySupport implements InformationCommentRepositoryCustom {

    public InformationCommentRepositoryImpl() {
        super(Information.class);
    }

    QInformation information = QInformation.information;
    QInformationComment informationComment = QInformationComment.informationComment;

    @Override
    public PageImpl<InformationCommentListResponse> getPageInformationComment(Pageable pageable, Long informationId) {

        long total = from(informationComment)
                .leftJoin(information).on(information.id.eq(informationComment.information.id))
                .where(informationComment.information.id.eq(informationId))
                .distinct()
                .fetchCount();

        List<InformationCommentListResponse> list = from(informationComment)
                .leftJoin(information).on(information.id.eq(informationComment.information.id))
                .where(informationComment.information.id.eq(informationId))
                .groupBy(informationComment.id)
                .select(Projections.constructor(
                        InformationCommentListResponse.class,
                        informationComment.id,
                        informationComment.user.id,
                        informationComment.user.nickname,
                        informationComment.user.userImage.address,
                        informationComment.content,
                        informationComment.createdDate,
                        informationComment.updatedDate
                )).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(list, pageable, total);
    }
}
