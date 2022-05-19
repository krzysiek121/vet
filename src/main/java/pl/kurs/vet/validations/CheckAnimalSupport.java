package pl.kurs.vet.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckAnimalValidator.class)
public @interface CheckAnimalSupport {

        String message() default "ANIMAL_NOT_SUPPORT";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
}
