package solitour_backend.solitour.auth.exception;

public class UnsupportedLoginTypeException extends RuntimeException {
    public UnsupportedLoginTypeException(String message) {
        super(message);
    }
}
