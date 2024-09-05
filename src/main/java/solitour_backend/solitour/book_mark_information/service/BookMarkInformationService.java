package solitour_backend.solitour.book_mark_information.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.book_mark_gathering.entity.BookMarkGathering;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformation;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformationRepository;
import solitour_backend.solitour.book_mark_information.exception.InformationBookMarkNotExistsException;
import solitour_backend.solitour.book_mark_information.service.dto.response.BookMarkInformationResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.repository.InformationRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookMarkInformationService {

    private final BookMarkInformationRepository bookMarkInformationRepository;
    private final UserRepository userRepository;
    private final InformationRepository informationRepository;

    public BookMarkInformation createUserBookmark(Long userId, Long infoId) {
        User user = userRepository.findByUserId(userId);
        Information information = informationRepository.findById(infoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정보가 없습니다."));

        return bookMarkInformationRepository.findByInformationIdAndUserId(infoId, userId)
                .orElseGet(() -> bookMarkInformationRepository.save(new BookMarkInformation(user, information)));
    }

    public void deleteUserBookmark(Long userId, Long infoId) {
        BookMarkInformation bookmark = bookMarkInformationRepository.findByInformationIdAndUserId(infoId,
                        userId)
                .orElseThrow(() -> new InformationBookMarkNotExistsException("해당하는 북마크가 없습니다"));

        bookMarkInformationRepository.delete(bookmark);
    }

}
