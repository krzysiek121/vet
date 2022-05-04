package pl.kurs.vet.validations;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import pl.kurs.vet.request.CreateCheckVisitCommand;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.AssertTrue;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
@Component
public class CheckDatesValidation implements ConstraintValidator<CheckDates, CreateCheckVisitCommand> {


    @Override
    public void initialize(CheckDates constraintAnnotation) {

    }

    @Override
    public boolean isValid(final CreateCheckVisitCommand visitCommand,final ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("walidacja");
        LocalDateTime start = visitCommand.getFrom();
        LocalDateTime stop = visitCommand.getTo();
        System.out.println(start.isBefore(stop));
        return start.isBefore(stop);

    }
}
