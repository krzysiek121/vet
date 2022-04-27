package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class DoctorNotWorkingException extends RuntimeException{
    private String message;
}
