package solitour_backend.solitour.error.exception;

import jakarta.validation.ValidationException;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;

public class RequestValidationFailedException extends ValidationException {

  public RequestValidationFailedException(BindingResult bindingResult) {
    super(bindingResult.getAllErrors()
        .stream()
        .map(objectError -> new StringBuilder()
            .append("object: ")
            .append(objectError.getObjectName())
            .append(", message: ")
            .append(objectError.getDefaultMessage())
            .append(", error code: ")
            .append(objectError.getCode()))
        .collect(Collectors.joining("|")));
  }
}
