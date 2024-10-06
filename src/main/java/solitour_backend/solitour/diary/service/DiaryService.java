package solitour_backend.solitour.diary.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.diary.diary_day_content.DiaryDayContent;
import solitour_backend.solitour.diary.dto.request.DiaryCreateRequest;
import solitour_backend.solitour.diary.dto.request.DiaryCreateRequest.DiaryDayRequest;
import solitour_backend.solitour.diary.dto.request.DiaryUpdateRequest;
import solitour_backend.solitour.diary.dto.request.DiaryUpdateRequest.DiaryUpdateDayRequest;
import solitour_backend.solitour.diary.dto.response.DiaryContent;
import solitour_backend.solitour.diary.dto.response.DiaryResponse;
import solitour_backend.solitour.diary.entity.Diary;
import solitour_backend.solitour.diary.exception.DiaryNotExistsException;
import solitour_backend.solitour.diary.feeling_status.FeelingStatus;
import solitour_backend.solitour.diary.repository.DiaryDayContentRepository;
import solitour_backend.solitour.diary.repository.DiaryRepository;
import solitour_backend.solitour.error.exception.ForbiddenAccessException;
import solitour_backend.solitour.image.s3.S3Uploader;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;
    private final DiaryDayContentRepository diaryDayContentRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public Long createDiary(Long userId, DiaryCreateRequest request) {
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

    public Page<DiaryContent> getAllDiary(Pageable pageable, Long userId) {
        return diaryRepository.getAllDiaryPageFilterAndOrder(pageable, userId);
    }

    public DiaryResponse getDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotExistsException("해당하는 일기가 존재하지 않습니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new ForbiddenAccessException("해당 일기에 대한 권한이 없습니다.");
        }

        return new DiaryResponse(diary);
    }

    @Transactional
    public void deleteDiary(Long userId, Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotExistsException("해당 일기가 존재하지 않습니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new ForbiddenAccessException("해당 일기에 대한 권한이 없습니다.");
        }

        deleteAllDiaryImage(diary.getDiaryDayContent());
        diaryRepository.deleteById(diaryId);
    }

    @Transactional
    public void updateDiary(Long userId, Long diaryId, DiaryUpdateRequest request) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotExistsException("해당 일기가 존재하지 않습니다."));

        if (!diary.getUser().getId().equals(userId)) {
            throw new ForbiddenAccessException("해당 일기에 대한 권한이 없습니다.");
        }

        updateDiary(diaryId, request);
    }

    private void updateDiary(Long diaryId, DiaryUpdateRequest request) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new DiaryNotExistsException("해당 일기가 존재하지 않습니다."));
        deleteDiaryImage(request);
        diary.getDiaryDayContent().clear();
        diary.updateDiary(request);
        updateDiaryDayContent(diary, request);
    }

    private void saveDiaryDayContent(Diary savedDiary, DiaryCreateRequest request) {
        for (DiaryDayRequest dayRequest : request.getDiaryDayRequests()) {
            makeDiaryImagePermanent(dayRequest.getDiaryDayContentImages());
            DiaryDayContent diaryDayContent = DiaryDayContent.builder()
                    .diary(savedDiary)
                    .content(dayRequest.getContent())
                    .contentImage(dayRequest.getDiaryDayContentImages())
                    .feelingStatus(FeelingStatus.valueOf(dayRequest.getFeelingStatus()))
                    .place(dayRequest.getPlace())
                    .build();
            diaryDayContentRepository.save(diaryDayContent);
        }
    }

    private void makeDiaryImagePermanent(String diaryDayContentImages) {
        if (!diaryDayContentImages.isEmpty()) {
            String[] contentImages = diaryDayContentImages.split(",");
            for (String contentImage : contentImages) {
                s3Uploader.markImagePermanent(contentImage);
            }
        }
    }

    private void updateDiaryDayContent(Diary savedDiary, DiaryUpdateRequest request) {
        diaryDayContentRepository.deleteById(savedDiary.getId());
        for (DiaryUpdateDayRequest dayRequest : request.getDiaryDayRequests()) {
            DiaryDayContent diaryDayContent = DiaryDayContent.builder()
                    .diary(savedDiary)
                    .content(dayRequest.getContent())
                    .contentImage(dayRequest.getSaveImagesUrl())
                    .feelingStatus(FeelingStatus.valueOf(dayRequest.getFeelingStatus()))
                    .place(dayRequest.getPlace())
                    .build();
            diaryDayContentRepository.save(diaryDayContent);
        }
    }

    private void deleteDiaryImage(DiaryUpdateRequest request) {
        if (request.getDeleteTitleImage() != "") {
            s3Uploader.deleteImage(request.getDeleteTitleImage());
        }

        for (DiaryUpdateDayRequest dayRequest : request.getDiaryDayRequests()) {
            for (String imageUrl : dayRequest.getSplitImageUrl(dayRequest.getDeleteImagesUrl())) {
                s3Uploader.deleteImage(imageUrl);
            }
        }
    }

    private void deleteAllDiaryImage(List<DiaryDayContent> diaryDayContent) {
        for (DiaryDayContent content : diaryDayContent) {
            for (String imageUrl : content.getDiaryDayContentImagesList()) {
                s3Uploader.deleteImage(imageUrl);
            }
        }
    }
}
