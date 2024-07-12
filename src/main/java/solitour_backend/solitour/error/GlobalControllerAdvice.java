package solitour_backend.solitour.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.image.exception.ImageNotExistsException;
import solitour_backend.solitour.information.exception.InformationNotExistsException;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryAlreadyExistsException;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryNotExistsException;

@RestControllerAdvice
public class GlobalControllerAdvice {

  @ExceptionHandler(RequestValidationFailedException.class)
  public ResponseEntity<String> validationException(Exception exception) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(exception.getMessage());
  }

  @ExceptionHandler(ZoneCategoryAlreadyExistsException.class)
  public ResponseEntity<String> conflictException(Exception exception) {
    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(exception.getMessage());
  }

  @ExceptionHandler({ZoneCategoryNotExistsException.class, ImageNotExistsException.class,
      CategoryNotExistsException.class, InformationNotExistsException.class})
  public ResponseEntity<String> exception(Exception exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(exception.getMessage());
  }
}
