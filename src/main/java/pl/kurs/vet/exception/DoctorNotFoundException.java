package pl.kurs.vet.exception;

import lombok.Value;

@Value
public class DoctorNotFoundException extends RuntimeException{
    private int id;
}
