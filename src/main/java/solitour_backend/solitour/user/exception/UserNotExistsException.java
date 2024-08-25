package solitour_backend.solitour.user.exception;

public class UserNotExistsException extends RuntimeException {

    public UserNotExistsException(String message) {
        super(message);
    }
}
