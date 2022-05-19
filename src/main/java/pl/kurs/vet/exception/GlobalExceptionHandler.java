package pl.kurs.vet.exception;

import lombok.Builder;
import lombok.Value;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler  {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        return new ResponseEntity(
                exc.getFieldErrors().stream()
                        .map(fe -> ErrorDtoId.builder()
                                .field(fe.getField())
                                .message(fe.getDefaultMessage())
                                .build()).collect(Collectors.toList())
                , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TooLateConfirmException.class)
    public ResponseEntity handlePersonNotFoundException(TooLateConfirmException exc) {
        return ResponseEntity.badRequest().body(new ErrorDto(exc.getMessage()));
    }

    @ExceptionHandler(DoctorNotFoundException.class)
    public ResponseEntity handleDoctorNotFoundException(DoctorNotFoundException exc) {
        return new ResponseEntity(new IdNotFound("DOCTOR_ID_NOT_FOUND", exc.getId()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PatientNotFoundException.class)
    public ResponseEntity handlePatientNotFoundException(PatientNotFoundException exc) {
        return new ResponseEntity(new IdNotFound("PATIENT_ID_NOT_FOUND", exc.getId()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TimeVisitException.class)
    public ResponseEntity handleTimeVisitException(TimeVisitException exc) {
        return new ResponseEntity(new ErrorDto(exc.getMessage()), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(StaleObjectStateException.class)
    public ResponseEntity handleStaleObjectStateException(StaleObjectStateException exc) {
        String shortEntityName = exc.getEntityName().substring(exc.getEntityName().lastIndexOf('.') + 1);
        return ResponseEntity.badRequest().body(new OptimisticLockDto(shortEntityName, (Integer) exc.getIdentifier()));
    }

    @ExceptionHandler({TokenNotFoundException.class, DateWrongException.class, NoEmptySlotsException.class})
    public ResponseEntity handleTokenNotFoundException(Exception exc) {
        return new ResponseEntity(new ErrorDto(exc.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity handleDateTimeParseException(Exception exc) {
        return ResponseEntity.badRequest().body(new ErrorDto("WRONG_INPUT_DATE_FORMAT"));
    }

    @ExceptionHandler(DoctorNotWorkingException.class)
    public ResponseEntity handleDoctorNotWorkingException(DoctorNotWorkingException exc) {
        return new ResponseEntity(new ErrorDto(exc.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @Value
    @Builder
    static class ErrorDto {
        private String message;
    }

    @Value
    @Builder
    static class IdNotFound {
        private String message;
        private int id;
    }

    @Value
    @Builder
    static class ErrorDtoId {
        private String field;
        private String message;
    }

    @Value
    @Builder
    static class OptimisticLockDto {
        private String entityName;
        private int id;
        private final String errorCode = "VERSION_TOO_OLD";
    }
}
