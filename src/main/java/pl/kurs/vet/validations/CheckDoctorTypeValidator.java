package pl.kurs.vet.validations;

import lombok.RequiredArgsConstructor;
import pl.kurs.vet.repository.DoctorRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@RequiredArgsConstructor
public class CheckDoctorTypeValidator implements ConstraintValidator<CheckDoctorTypeSupport, String> {

    private final DoctorRepository doctorRepository;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return doctorRepository.existsByType(s);
    }
}
