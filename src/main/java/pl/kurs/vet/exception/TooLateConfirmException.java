package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class TooLateConfirmException extends RuntimeException {
    private String message;
}
