package solitour_backend.solitour.book_mark_gathering.dto.response;

import java.util.List;
import lombok.Getter;
import solitour_backend.solitour.book_mark_gathering.entity.BookMarkGathering;

@Getter
public class BookMarkGatheringResponse {

    private final BookMarkDto bookMarkGathering;

    public BookMarkGatheringResponse(List<BookMarkGathering> bookMarkGathering) {
        this.bookMarkGathering = new BookMarkDto(bookMarkGathering);
    }

    @Getter
    private static class BookMarkDto {

        private final List<BookMarkInfo> bookMarkInfoList;

        private BookMarkDto(List<BookMarkGathering> bookMarkGathering) {
            this.bookMarkInfoList = bookMarkGathering.stream().map(bookMark -> {
                Long bookMarkId = bookMark.getGathering().getId();
                return new BookMarkInfo(bookMarkId);
            }).toList();
        }

    }
}
