package solitour_backend.solitour.information.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import solitour_backend.solitour.information.dto.request.InformationPageRequest;
import solitour_backend.solitour.information.dto.response.InformationBriefResponse;

public interface InformationNativeRepository {
    Page<InformationBriefResponse> searchByFullTextIndex(Pageable pageable,
                                                         InformationPageRequest request,
                                                         Long userId,
                                                         Long parentCategoryId);
}