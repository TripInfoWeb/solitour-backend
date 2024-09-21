package solitour_backend.solitour.user.exception;

public class DeletedUserException extends RuntimeException {

    public DeletedUserException(String message) {
        super(message);
    }
}
