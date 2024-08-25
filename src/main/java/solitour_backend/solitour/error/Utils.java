package solitour_backend.solitour.error;

import org.springframework.validation.BindingResult;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;

public class Utils {

    private Utils() {
    }

    public static void validationRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new RequestValidationFailedException(bindingResult);
        }
    }
}
