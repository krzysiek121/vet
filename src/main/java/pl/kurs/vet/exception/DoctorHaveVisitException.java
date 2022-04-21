package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class DoctorHaveVisitException extends RuntimeException{
    private String data;
}
