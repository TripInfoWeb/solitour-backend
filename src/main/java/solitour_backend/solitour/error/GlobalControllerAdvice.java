package solitour_backend.solitour.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.gathering.exception.GatheringCategoryNotExistsException;
import solitour_backend.solitour.gathering.exception.GatheringNotExistsException;
import solitour_backend.solitour.image.exception.ImageAlreadyExistsException;
import solitour_backend.solitour.image.exception.ImageNotExistsException;
import solitour_backend.solitour.image.exception.ImageRequestValidationFailedException;
import solitour_backend.solitour.information.exception.InformationNotExistsException;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryAlreadyExistsException;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryNotExistsException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({RequestValidationFailedException.class, ImageRequestValidationFailedException.class})
    public ResponseEntity<String> validationException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({ZoneCategoryAlreadyExistsException.class, ImageAlreadyExistsException.class})
    public ResponseEntity<String> conflictException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(exception.getMessage());
    }

    @ExceptionHandler({
            ZoneCategoryNotExistsException.class,
            ImageNotExistsException.class,
            CategoryNotExistsException.class,
            InformationNotExistsException.class,
            UserNotExistsException.class,
            GatheringCategoryNotExistsException.class,
            GatheringNotExistsException.class})
    public ResponseEntity<String> notFoundException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }
}
