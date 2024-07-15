package solitour_backend.solitour.error.exception;

import jakarta.validation.ValidationException;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

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
