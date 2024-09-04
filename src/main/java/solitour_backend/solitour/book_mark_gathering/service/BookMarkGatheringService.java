package solitour_backend.solitour.book_mark_gathering.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.book_mark_gathering.dto.response.BookMarkGatheringResponse;
import solitour_backend.solitour.book_mark_gathering.entity.BookMarkGathering;
import solitour_backend.solitour.book_mark_gathering.repository.BookMarkGatheringRepository;
import solitour_backend.solitour.gathering.entity.Gathering;
import solitour_backend.solitour.gathering.repository.GatheringRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookMarkGatheringService {

    private final BookMarkGatheringRepository bookMarkGatheringRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;

    public void createUserBookmark(Long userId, Long gatheringId) {
        User user = userRepository.findByUserId(userId);
        Gathering gathering = gatheringRepository.findById(gatheringId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 정보가 없습니다"));
        BookMarkGathering bookMarkGathering = new BookMarkGathering(user, gathering);

        bookMarkGatheringRepository.save(bookMarkGathering);
    }

    public void deleteUserBookmark(Long userId, Long gatheringId) {
        BookMarkGathering bookmark = bookMarkGatheringRepository.findByGatheringIdAndUserId(gatheringId,
                        userId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 북마크가 없습니다"));

        bookMarkGatheringRepository.delete(bookmark);
    }

}
