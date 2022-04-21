package pl.kurs.vet.validations;

import lombok.RequiredArgsConstructor;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.repository.DoctorReposirtory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CheckAnimalValidator implements ConstraintValidator<CheckAnimalSupport, String> {

    private final DoctorReposirtory doctorReposirtory;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !doctorReposirtory.findByAnimalType(s).isEmpty();
    }
}
