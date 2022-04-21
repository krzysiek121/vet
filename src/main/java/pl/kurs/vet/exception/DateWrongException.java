package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class DateWrongException extends RuntimeException {
    private String message;
}
