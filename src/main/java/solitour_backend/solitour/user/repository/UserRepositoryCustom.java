package solitour_backend.solitour.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.gathering.dto.response.GatheringApplicantResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringBriefResponse;
import solitour_backend.solitour.gathering.dto.response.GatheringMypageResponse;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;


@NoRepositoryBean
public interface UserRepositoryCustom {

    Page<InformationBriefResponse> retrieveInformationOwner(Pageable pageable, Long userId);

    Page<InformationBriefResponse> retrieveInformationBookmark(Pageable pageable, Long userId);

    Page<GatheringMypageResponse> retrieveGatheringHost(Pageable pageable, Long userId);

    Page<GatheringMypageResponse> retrieveGatheringBookmark(Pageable pageable, Long userId);

    Page<GatheringApplicantResponse> retrieveGatheringApplicant(Pageable pageable, Long userId);

    String getProfileUrl(String gender);
}
