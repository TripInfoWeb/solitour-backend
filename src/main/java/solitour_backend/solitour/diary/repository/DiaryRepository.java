package solitour_backend.solitour.diary.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import solitour_backend.solitour.diary.entity.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryRepositoryCustom {
    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId")
    List<Diary> findByUserId(Long userId);

    @Query("DELETE FROM Diary d WHERE d.user.id = :userId")
    void deleteByUserId(Long userId);
}
