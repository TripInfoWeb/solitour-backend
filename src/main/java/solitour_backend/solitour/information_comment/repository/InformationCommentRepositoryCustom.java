package solitour_backend.solitour.information_comment.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.information_comment.dto.respose.InformationCommentListResponse;


@NoRepositoryBean
public interface InformationCommentRepositoryCustom {
    PageImpl<InformationCommentListResponse> getPageInformationComment(Pageable pageable, Long id);
}
