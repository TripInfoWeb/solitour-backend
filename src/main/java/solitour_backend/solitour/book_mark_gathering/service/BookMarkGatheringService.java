package solitour_backend.solitour.book_mark_gathering.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.book_mark_gathering.entity.BookMarkGathering;
import solitour_backend.solitour.book_mark_gathering.exception.GatheringBookMarkNotExistsException;
import solitour_backend.solitour.book_mark_gathering.repository.BookMarkGatheringRepository;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.gathering.repository.GatheringRepository;
import solitour_backend.solitour.information.exception.InformationNotExistsException;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookMarkGatheringService {

    private final BookMarkGatheringRepository bookMarkGatheringRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;

    @Transactional
    public BookMarkGathering createUserBookmark(Long userId, Long gatheringId) {
        User user = userRepository.findByUserId(userId);
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new InformationNotExistsException("해당하는 정보가 없습니다"));

        return bookMarkGatheringRepository.findByGatheringIdAndUserId(gatheringId, userId)
                .orElseGet(
                        () -> bookMarkGatheringRepository.save(new BookMarkGathering(user, gathering)));
    }

    @Transactional
    public void deleteUserBookmark(Long userId, Long gatheringId) {
        BookMarkGathering bookmark = bookMarkGatheringRepository.findByGatheringIdAndUserId(gatheringId,
                        userId)
                .orElseThrow(() -> new GatheringBookMarkNotExistsException("해당하는 북마크가 없습니다"));

        bookMarkGatheringRepository.delete(bookmark);
    }

}
