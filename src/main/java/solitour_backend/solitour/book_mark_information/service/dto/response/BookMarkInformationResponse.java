package solitour_backend.solitour.book_mark_information.service.dto.response;

import java.util.List;
import lombok.Getter;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformation;

@Getter
public class BookMarkInformationResponse {

  private final List<BookMarkInformation> bookMarkInformation;

  public BookMarkInformationResponse(List<BookMarkInformation> bookMarkInformation) {
    this.bookMarkInformation = bookMarkInformation;
  }
}
