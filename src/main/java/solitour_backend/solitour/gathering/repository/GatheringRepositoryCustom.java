package solitour_backend.solitour.gathering.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.gathering.dto.request.GatheringPageRequest;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringRankResponse;

@NoRepositoryBean
public interface GatheringRepositoryCustom {
    String LIKE_COUNT_SORT = "likes";
    String VIEW_COUNT_SORT = "views";

    List<GatheringBriefResponse> getGatheringRecommend(Long informationId, Long gatheringCategoryId, Long userId);

    Page<GatheringBriefResponse> getGatheringPageFilterAndOrder(Pageable pageable,
                                                                GatheringPageRequest gatheringPageRequest, Long userId);

    List<GatheringRankResponse> getGatheringRankList();

    List<GatheringBriefResponse> getGatheringLikeCountFromCreatedIn3(Long userId);

    Page<GatheringBriefResponse> getPageGatheringByTag(Pageable pageable, GatheringPageRequest gatheringPageRequest,
                                                       Long userId, String decodedTag);
}
