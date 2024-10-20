package solitour_backend.solitour.information_comment.exception;

public class CommentNotOwnerException extends RuntimeException {

    public CommentNotOwnerException(String message) {
        super(message);
    }
}
