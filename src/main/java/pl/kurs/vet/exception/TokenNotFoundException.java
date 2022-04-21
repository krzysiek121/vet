package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class TokenNotFoundException extends RuntimeException {
    private String message;
}
