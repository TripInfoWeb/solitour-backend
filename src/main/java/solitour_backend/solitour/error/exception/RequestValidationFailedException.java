package solitour_backend.solitour.error.exception;

import jakarta.validation.ValidationException;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;

public class RequestValidationFailedException extends ValidationException {

    public RequestValidationFailedException(BindingResult bindingResult) {
        super(bindingResult.getAllErrors()
                .stream()
                .map(objectError -> new StringBuilder()
                        .append("Object: ")
                        .append(objectError.getObjectName())
                        .append("\nMessage: ")
                        .append(objectError.getDefaultMessage())
                        .append("\nError Code: ")
                        .append(objectError.getCode())
                        .append("\n")
                        .toString())
                .collect(Collectors.joining("\n-----------------------------\n")));
    }


    public RequestValidationFailedException(String message) {
        super(message);
    }
}
