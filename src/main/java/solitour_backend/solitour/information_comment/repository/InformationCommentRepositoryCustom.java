package solitour_backend.solitour.information_comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentListResponse;
import solitour_backend.solitour.information_comment.entity.InformationComment;

import java.util.List;


@NoRepositoryBean
public interface InformationCommentRepositoryCustom {
    PageImpl<InformationCommentListResponse> getPageInformationComment(Pageable pageable, Long id);
}
