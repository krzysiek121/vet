package pl.kurs.vet.validations;

import lombok.RequiredArgsConstructor;
import pl.kurs.vet.repository.PatientRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@RequiredArgsConstructor
public class CheckEmailValidator implements ConstraintValidator<CheckEmail, String> {

    private final PatientRepository patientRepository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return !patientRepository.existsByEmail(s);
    }
}
