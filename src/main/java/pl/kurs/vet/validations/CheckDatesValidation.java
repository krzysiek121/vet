package pl.kurs.vet.validations;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import pl.kurs.vet.request.CreateCheckVisitCommand;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDatesValidation implements ConstraintValidator<CheckDates, CreateCheckVisitCommand> {


    @Override
    public void initialize(CheckDates constraintAnnotation) {

    }

    @Override
    public boolean isValid(final CreateCheckVisitCommand visitCommand,final ConstraintValidatorContext constraintValidatorContext) {

        LocalDateTime start = visitCommand.getFrom();
        LocalDateTime stop = visitCommand.getTo();

        constraintValidatorContext.buildConstraintViolationWithTemplate("CHECK_INPUT_DATE_TO")
                .addPropertyNode("to")
                .addConstraintViolation();
        return start.isBefore(stop);

    }
}
