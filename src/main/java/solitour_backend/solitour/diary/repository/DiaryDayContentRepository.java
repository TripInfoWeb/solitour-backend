package solitour_backend.solitour.diary.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solitour_backend.solitour.diary.diary_day_content.DiaryDayContent;
import solitour_backend.solitour.diary.entity.Diary;
import solitour_backend.solitour.user.entity.User;

public interface DiaryDayContentRepository extends JpaRepository<DiaryDayContent, Long> {
}
