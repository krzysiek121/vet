package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class NoEmptySlotsException extends RuntimeException {
    private String message;
}
