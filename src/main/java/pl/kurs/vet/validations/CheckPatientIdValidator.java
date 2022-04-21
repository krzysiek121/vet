package pl.kurs.vet.validations;

import lombok.RequiredArgsConstructor;
import pl.kurs.vet.repository.PatientRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@RequiredArgsConstructor
public class CheckPatientIdValidator implements ConstraintValidator<CheckPatientId, Integer> {

    private final PatientRepository patientRepository;

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return patientRepository.existsById(integer);
    }
}
