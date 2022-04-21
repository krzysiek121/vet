package pl.kurs.vet.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CheckDateValidator implements ConstraintValidator<CheckDate, String> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        LocalDateTime localDateTime = LocalDateTime.parse(s, formatter);

        return localDateTime.isAfter(LocalDateTime.now()) || localDateTime.isEqual(LocalDateTime.now());
    }
}
