package solitour_backend.solitour.image.exception;

public class ImageRequestValidationFailedException extends RuntimeException {

    public ImageRequestValidationFailedException(String message) {
        super(message);
    }
}
