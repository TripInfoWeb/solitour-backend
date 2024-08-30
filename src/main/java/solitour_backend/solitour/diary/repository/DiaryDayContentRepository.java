package solitour_backend.solitour.diary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import solitour_backend.solitour.diary.diary_day_content.DiaryDayContent;

public interface DiaryDayContentRepository extends JpaRepository<DiaryDayContent, Long> {
}
