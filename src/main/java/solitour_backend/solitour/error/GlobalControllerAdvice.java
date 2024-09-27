package solitour_backend.solitour.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import solitour_backend.solitour.auth.exception.TokenNotExistsException;
import solitour_backend.solitour.auth.exception.UnsupportedLoginTypeException;
import solitour_backend.solitour.auth.exception.UserRevokeErrorException;
import solitour_backend.solitour.book_mark_gathering.exception.GatheringBookMarkNotExistsException;
import solitour_backend.solitour.book_mark_information.exception.InformationBookMarkNotExistsException;
import solitour_backend.solitour.category.exception.CategoryNotExistsException;
import solitour_backend.solitour.diary.exception.DiaryNotExistsException;
import solitour_backend.solitour.error.exception.ForbiddenAccessException;
import solitour_backend.solitour.error.exception.RequestValidationFailedException;
import solitour_backend.solitour.gathering.exception.GatheringCategoryNotExistsException;
import solitour_backend.solitour.gathering.exception.GatheringDeleteException;
import solitour_backend.solitour.gathering.exception.GatheringNotExistsException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsAlreadyExistsException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsAlreadyFullPeopleException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsManagerException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringApplicantsNotExistsException;
import solitour_backend.solitour.gathering_applicants.exception.GatheringNotManagerException;
import solitour_backend.solitour.great_gathering.exception.GatheringGreatNotExistsException;
import solitour_backend.solitour.great_information.exception.InformationGreatNotExistsException;
import solitour_backend.solitour.image.exception.ImageAlreadyExistsException;
import solitour_backend.solitour.image.exception.ImageNotExistsException;
import solitour_backend.solitour.image.exception.ImageRequestValidationFailedException;
import solitour_backend.solitour.information.exception.InformationNotExistsException;
import solitour_backend.solitour.information.exception.InformationNotManageException;
import solitour_backend.solitour.user.exception.BlockedUserException;
import solitour_backend.solitour.user.exception.DeletedUserException;
import solitour_backend.solitour.user.exception.DormantUserException;
import solitour_backend.solitour.user.exception.UserNotExistsException;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryAlreadyExistsException;
import solitour_backend.solitour.zone_category.exception.ZoneCategoryNotExistsException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler({
            RequestValidationFailedException.class,
            ImageRequestValidationFailedException.class,
            GatheringApplicantsManagerException.class,
            InformationNotManageException.class,
            UnsupportedLoginTypeException.class
    })
    public ResponseEntity<String> validationException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }

    @ExceptionHandler({
            ZoneCategoryAlreadyExistsException.class,
            ImageAlreadyExistsException.class,
            GatheringApplicantsAlreadyExistsException.class,
            GatheringApplicantsAlreadyFullPeopleException.class
    })
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
            GatheringNotExistsException.class,
            GatheringApplicantsNotExistsException.class,
            GatheringDeleteException.class,
            InformationGreatNotExistsException.class,
            InformationGreatNotExistsException.class,
            GatheringGreatNotExistsException.class,
            GatheringBookMarkNotExistsException.class,
            InformationBookMarkNotExistsException.class,
            DiaryNotExistsException.class,
    })
    public ResponseEntity<String> notFoundException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception.getMessage());
    }

    @ExceptionHandler({GatheringNotManagerException.class,
            ForbiddenAccessException.class,
            BlockedUserException.class,
            DeletedUserException.class
    })
    public ResponseEntity<String> forbiddenException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(exception.getMessage());
    }

    @ExceptionHandler({
            DormantUserException.class
    })
    public ResponseEntity<String> dormantException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(exception.getMessage());
    }

    @ExceptionHandler({
            TokenNotExistsException.class
    })
    public ResponseEntity<String> unauthorizedException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(exception.getMessage());
    }
    @ExceptionHandler({
            UserRevokeErrorException.class
    })
    public ResponseEntity<String> serverErrorException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }

}
