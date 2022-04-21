package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class PatientNotFoundException extends RuntimeException{
    private int id;
}
