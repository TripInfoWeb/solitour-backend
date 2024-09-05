package solitour_backend.solitour.auth.exception;

public class TokenNotExistsException extends RuntimeException {
    public TokenNotExistsException(String message) {
        super(message);
    }
}
