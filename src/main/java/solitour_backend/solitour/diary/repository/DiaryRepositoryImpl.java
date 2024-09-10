package solitour_backend.solitour.diary.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import solitour_backend.solitour.diary.diary_day_content.QDiaryDayContent;
import solitour_backend.solitour.diary.dto.response.DiaryContent;
import solitour_backend.solitour.diary.dto.response.DiaryContent.DiaryDayContentResponse;
import solitour_backend.solitour.diary.entity.Diary;
import solitour_backend.solitour.diary.entity.QDiary;
import solitour_backend.solitour.diary.exception.DiaryNotExistsException;
import solitour_backend.solitour.user.entity.QUser;

public class DiaryRepositoryImpl extends QuerydslRepositorySupport implements DiaryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    private JPAQueryFactory queryFactory;

    public DiaryRepositoryImpl() {
        super(Diary.class);
    }

    @PostConstruct
    private void init() {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    QDiary diary = QDiary.diary;
    QDiaryDayContent diaryDayContent = QDiaryDayContent.diaryDayContent;
    QUser user = QUser.user;

    @Override
    public Page<DiaryContent> getAllDiaryPageFilterAndOrder(Pageable pageable, Long userId) {

        List<Diary> diaries = queryFactory
                .selectFrom(diary)
                .distinct()
                .join(diary.diaryDayContent, diaryDayContent)
                .join(diary.user, user)
                .where(diary.user.id.eq(userId))
                .orderBy(diary.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (diaries == null) {
            throw new DiaryNotExistsException("해당 일기가 존재하지 않습니다.");
        }

        List<DiaryContent> diaryContents = diaries.stream()
                .map(diary -> DiaryContent.builder()
                        .diaryId(diary.getId())
                        .title(diary.getTitle())
                        .titleImage(diary.getTitleImage())
                        .startDatetime(diary.getStartDatetime())
                        .endDatetime(diary.getEndDatetime())
                        .diaryDayContentResponses(new DiaryDayContentResponse(
                                diary.getDiaryDayContent()
                        ))
                        .build()).collect(Collectors.toList());

        long totalCount = queryFactory.selectFrom(diary)
                .distinct()
                .join(diary.diaryDayContent, diaryDayContent)
                .join(diary.user, user)
                .where(diary.user.id.eq(userId))
                .fetchCount();

        return new PageImpl<>(diaryContents, pageable, totalCount);
    }
}
