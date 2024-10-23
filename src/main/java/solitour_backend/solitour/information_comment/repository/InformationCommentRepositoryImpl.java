package solitour_backend.solitour.information_comment.repository;

import com.querydsl.core.types.Projections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.entity.QInformation;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentListResponse;
import solitour_backend.solitour.information_comment.entity.QInformationComment;

import java.util.List;

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
