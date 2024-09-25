package solitour_backend.solitour.auth.exception;

public class UserRevokeErrorException extends RuntimeException {
    public UserRevokeErrorException(String message) {
        super(message);
    }
}
