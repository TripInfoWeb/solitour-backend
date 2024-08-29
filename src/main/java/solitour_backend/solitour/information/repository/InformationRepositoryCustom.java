package solitour_backend.solitour.information.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;


@NoRepositoryBean
public interface InformationRepositoryCustom {
    String LIKE_COUNT_SORT = "likes";
    String VIEW_COUNT_SORT = "views";

    Page<InformationBriefResponse> getInformationPageFilterAndOrder(Pageable pageable,
                                                                    InformationPageRequest informationPageRequest,
                                                                    Long userId, Long parentCategoryId);

    List<InformationRankResponse> getInformationRank();

    List<InformationMainResponse> getInformationLikeCountFromCreatedIn3(Long userId);

    List<InformationBriefResponse> getInformationRecommend(Long informationId, Long childCategoryId, Long userId);

    Page<InformationBriefResponse> getInformationPageByTag(Pageable pageable, Long userId, Long parentCategoryId, InformationPageRequest informationPageRequest, String decodedTag);
}
