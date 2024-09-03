package solitour_backend.solitour.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.admin.dto.request.NoticeModifyRequest;
import solitour_backend.solitour.admin.dto.request.NoticeRegisterRequest;
import solitour_backend.solitour.admin.entity.Notice;
import solitour_backend.solitour.admin.repository.NoticeRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    // 공지사항 생성
    public Long createNotice(NoticeRegisterRequest noticeRegisterRequest, Long userId) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        if (!user.getIsAdmin()) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }

        Notice notice = noticeRepository.save(Notice.builder()
                .title(noticeRegisterRequest.getTitle())
                .content(noticeRegisterRequest.getContent())
                .categoryName(noticeRegisterRequest.getCategory())
                .build());

        return notice.getId();
    }

    // 공지사항 목록 조회
    public Page<Notice> getNotices(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("id")));
        return noticeRepository.findAllByIsDeletedFalse(pageable);
    }

    // 공지사항 목록 조회
    public Page<Notice> getNoticesForAdmin(int page, Long userId) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("id")));

        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        if (!user.getIsAdmin()) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }

            return noticeRepository.findAll(pageable);
    }

    // 공지사항 싱세 조회
    public Notice getNotice(Long id) {
        return noticeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
    }

    // 관리자를 위한 공지사항 조회, 관리자는 삭제된 공지사항도 볼 수 있음
    public Notice getNoticeForAdmin(Long userId, Long id) {

        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        if (!user.getIsAdmin()) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }

        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
    }

    // 공지사항 수정
    @Transactional
    public boolean updateNotice(Long userId, NoticeModifyRequest noticeModifyRequest) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        if (!user.getIsAdmin()) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }

        Notice notice = noticeRepository.findByIdAndIsDeletedFalse(noticeModifyRequest.getId())
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        notice.setTitle(noticeModifyRequest.getTitle());
        notice.setContent(noticeModifyRequest.getContent());
        notice.setCategoryName(noticeModifyRequest.getCategory());

        noticeRepository.save(notice);
        return true;
    }

    // 공지사항 삭제 (soft delete)
    @Transactional
    public boolean deleteNotice(Long userId, Long id) {
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        if (!user.getIsAdmin()) {
            throw new RuntimeException("관리자 권한이 필요합니다.");
        }

        Notice notice = noticeRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        notice.setIsDeleted(true);
        noticeRepository.save(notice);
        return true;
    }
}

