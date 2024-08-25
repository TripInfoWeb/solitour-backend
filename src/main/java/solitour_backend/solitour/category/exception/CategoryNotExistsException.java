package solitour_backend.solitour.category.exception;

public class CategoryNotExistsException extends RuntimeException {

    public CategoryNotExistsException(String message) {
        super(message);
    }
}
