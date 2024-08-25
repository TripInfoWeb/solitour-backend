package solitour_backend.solitour.book_mark_information.service.dto.response;

import java.util.List;
import lombok.Getter;
import solitour_backend.solitour.book_mark_information.entity.BookMarkInformation;

@Getter
public class BookMarkInformationResponse {

    private final BookMarkDto bookMarkInformation;

    public BookMarkInformationResponse(List<BookMarkInformation> bookMarkInformation) {
        this.bookMarkInformation = new BookMarkDto(bookMarkInformation);
    }

    @Getter
    private static class BookMarkDto {

        private final List<BookMarkInfo> bookMarkInfoList;

        private BookMarkDto(List<BookMarkInformation> bookMarkInformation) {
            this.bookMarkInfoList = bookMarkInformation.stream().map(bookMark -> {
                Long bookMarkId = bookMark.getInformation().getId();
                return new BookMarkInfo(bookMarkId);
            }).toList();
        }

    }
}
