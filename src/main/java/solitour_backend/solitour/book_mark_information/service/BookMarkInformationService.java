package solitour_backend.solitour.book_mark_information.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformation;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformationRepository;
import solitour_backend.solitour.book_mark_information.service.dto.response.BookMarkInformationResponse;
import solitour_backend.solitour.information.entity.Information;
import solitour_backend.solitour.information.repository.InformationRepository;
import solitour_backend.solitour.user.entity.User;
import solitour_backend.solitour.user.entity.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookMarkInformationService {

  private final BookMarkInformationRepository bookMarkInformationRepository;
  private final UserRepository userRepository;
  private final InformationRepository informationRepository;

  public BookMarkInformationResponse getUserBookmark(Long userId) {
    List<BookMarkInformation> bookMarkInformation = bookMarkInformationRepository.findByUserId(userId);

    return new BookMarkInformationResponse(bookMarkInformation);
  }

  public void createUserBookmark(Long userId,Long infoId) {
    User user = userRepository.findByUserId(userId);
    Information information = informationRepository.findById(infoId)
        .orElseThrow(() -> new IllegalArgumentException("해당 정보가 없습니다."));
    BookMarkInformation bookMarkInformation = new BookMarkInformation(user,information);

    bookMarkInformationRepository.save(bookMarkInformation);
  }

  public void deleteUserBookmark(Long bookMarkId) {
    bookMarkInformationRepository.deleteById(bookMarkId);
  }

}
