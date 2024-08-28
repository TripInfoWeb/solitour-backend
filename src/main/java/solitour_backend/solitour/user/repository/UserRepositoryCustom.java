package solitour_backend.solitour.user.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;
import solitour_backend.solitour.information.dto.response.InformationMainResponse;
import solitour_backend.solitour.information.dto.response.InformationRankResponse;


@NoRepositoryBean
public interface UserRepositoryCustom {

    Page<InformationBriefResponse> getInformationByUserId(Pageable pageable, Long userId);

    Page<InformationBriefResponse> getInformationByUserBookMark(Pageable pageable, Long userId);
}
