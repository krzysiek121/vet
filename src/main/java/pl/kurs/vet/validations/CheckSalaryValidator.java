package pl.kurs.vet.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CheckSalaryValidator implements ConstraintValidator<CheckSalary, Integer> {

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if (integer > 0) {
            return true;
        }
        return false;
    }
}
