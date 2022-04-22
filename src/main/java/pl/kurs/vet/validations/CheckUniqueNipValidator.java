package pl.kurs.vet.validations;

import lombok.RequiredArgsConstructor;
import pl.kurs.vet.repository.DoctorReposirtory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@RequiredArgsConstructor
public class CheckUniqueNipValidator implements ConstraintValidator<CheckUniqueNip, Integer> {

    private final DoctorReposirtory doctorReposirtory;

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return doctorReposirtory.existsByNip(integer);
    }
}
