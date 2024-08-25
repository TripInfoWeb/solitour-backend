package solitour_backend.solitour.diary.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.diary.diary_day_content.DiaryDayContent;
import solitour_backend.solitour.diary.dto.DiaryContent;
import solitour_backend.solitour.diary.dto.DiaryListResponse;
import solitour_backend.solitour.diary.dto.DiaryRequest;
import solitour_backend.solitour.diary.dto.DiaryRequest.DiaryDayRequest;
import solitour_backend.solitour.diary.dto.DiaryResponse;
import solitour_backend.solitour.diary.entity.Diary;
import solitour_backend.solitour.diary.feeling_status.FeelingStatus;
import solitour_backend.solitour.diary.repository.DiaryDayContentRepository;
import solitour_backend.solitour.diary.repository.DiaryRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.entity.UserRepository;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DiaryDayContentRepository diaryDayContentRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createDiary(Long userId, DiaryRequest request) {
        User user = userRepository.findByUserId(userId);
        Diary diary = Diary.builder()
                .user(user)
                .title(request.getTitle())
                .titleImage(request.getTitleImage())
                .startDatetime(request.getStartDatetime())
                .endDatetime(request.getEndDatetime())
                .createdAt(LocalDateTime.now())
                .build();

        Diary savedDiary = diaryRepository.save(diary);

        saveDiaryDayContent(savedDiary, request);

        return savedDiary.getId();
    }


    private void saveDiaryDayContent(Diary savedDiary, DiaryRequest request) {
        for (DiaryDayRequest dayRequest : request.getDiaryDayRequests()) {
            DiaryDayContent diaryDayContent = DiaryDayContent.builder()
                    .diary(savedDiary)
                    .content(dayRequest.getContent())
                    .feelingStatus(FeelingStatus.valueOf(dayRequest.getFeelingStatus()))
                    .place(dayRequest.getPlace())
                    .build();
            diaryDayContentRepository.save(diaryDayContent);
        }
    }

    public Page<DiaryContent>  getAllDiary(Pageable pageable, Long userId) {
        return diaryRepository.getAllDiaryPageFilterAndOrder(pageable,userId);
    }


    public DiaryResponse getDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 존재하지 않습니다."));

        if(!diary.getUser().getId().equals(userId)){
            throw new IllegalArgumentException("해당 일기에 대한 권한이 없습니다.");
        }

        return new DiaryResponse(diary);
    }

    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 존재하지 않습니다."));
        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 일기에 대한 권한이 없습니다.");
        }
        diaryRepository.deleteById(diaryId);
    }

    @Transactional
    public void updateDiary(Long userId, Long diaryId, DiaryRequest request) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 일기가 존재하지 않습니다."));
        if (!diary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 일기에 대한 권한이 없습니다.");
        }
        updateDiary(diaryId, request);
    }

    private void updateDiary(Long diaryId, DiaryRequest request) {
        Diary diary = diaryRepository.findById(diaryId).orElseThrow(() -> new RuntimeException("Diary not found"));
        diary.getDiaryDayContent().clear();
        diary.updateDiary(request);
        saveDiaryDayContent(diary, request);
    }

}