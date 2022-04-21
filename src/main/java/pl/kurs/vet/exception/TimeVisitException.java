package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class TimeVisitException extends RuntimeException {
    private String message;
}
