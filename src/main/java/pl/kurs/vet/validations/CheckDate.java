package pl.kurs.vet.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckDateValidator.class)
public @interface CheckDate {
    String message() default "WRONG_DATE";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
