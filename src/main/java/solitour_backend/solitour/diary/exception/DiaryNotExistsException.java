package solitour_backend.solitour.diary.exception;

public class DiaryNotExistsException extends RuntimeException {
    public DiaryNotExistsException(String message) {
        super(message);
    }
}
