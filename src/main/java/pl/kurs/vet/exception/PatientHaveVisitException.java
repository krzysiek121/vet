package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class PatientHaveVisitException extends RuntimeException{
    private String data;
}
