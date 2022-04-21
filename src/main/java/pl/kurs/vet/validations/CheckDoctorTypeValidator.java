package pl.kurs.vet.validations;

import lombok.RequiredArgsConstructor;
import pl.kurs.vet.repository.DoctorReposirtory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@RequiredArgsConstructor
public class CheckDoctorTypeValidator implements ConstraintValidator<CheckDoctorTypeSupport, String> {

    private final DoctorReposirtory doctorReposirtory;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !doctorReposirtory.findByType(s).isEmpty();
    }
}
