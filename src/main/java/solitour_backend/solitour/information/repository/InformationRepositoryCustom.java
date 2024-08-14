package solitour_backend.solitour.information.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;


@NoRepositoryBean
public interface InformationRepositoryCustom {

    Page<InformationBriefResponse> getInformationByParentCategoryFilterZoneCategory(Pageable pageable,
                                                                                    Long parentCategoryId, Long userId,
                                                                                    Long zoneCategoryId);

    Page<InformationBriefResponse> getInformationByChildCategoryFilterZoneCategory(Pageable pageable,
                                                                                   Long childCategoryId, Long userId,
                                                                                   Long zoneCategoryId);

    Page<InformationBriefResponse> getInformationByParentCategoryFilterZoneCategoryLikeCount(Pageable pageable,
                                                                                             Long categoryId,
                                                                                             Long userId,
                                                                                             Long zoneCategoryId);

    Page<InformationBriefResponse> getInformationByChildCategoryFilterZoneCategoryLikeCount(Pageable pageable,
                                                                                            Long categoryId,
                                                                                            Long userId,
                                                                                            Long zoneCategoryId);

    Page<InformationBriefResponse> getInformationByParentCategoryFilterZoneCategoryViewCount(Pageable pageable,
                                                                                             Long categoryId,
                                                                                             Long userId,
                                                                                             Long zoneCategoryId);

    Page<InformationBriefResponse> getInformationByChildCategoryFilterZoneCategoryViewCount(Pageable pageable,
                                                                                            Long categoryId,
                                                                                            Long userId,
                                                                                            Long zoneCategoryId);

    List<InformationRankResponse> getInformationRank();

    List<InformationMainResponse> getInformationLikeCountFromCreatedIn3(Long userId);

    List<InformationBriefResponse> getInformationRecommend(Long informationId, Long childCategoryId, Long userId);

    Page<InformationBriefResponse> getInformationByParentCategoryFilterTag(Pageable pageable, Long parentCategoryId, Long userId, String tag);

    Page<InformationBriefResponse> getInformationByChildCategoryFilterTag(Pageable pageable, Long childCategoryId, Long userId, String tag);

}
