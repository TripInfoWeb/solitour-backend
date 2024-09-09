package solitour_backend.solitour.diary.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.NoRepositoryBean;
import solitour_backend.solitour.diary.dto.response.DiaryContent;

@NoRepositoryBean
public interface DiaryRepositoryCustom {
    Page<DiaryContent> getAllDiaryPageFilterAndOrder(Pageable pageable, Long userId);
}
