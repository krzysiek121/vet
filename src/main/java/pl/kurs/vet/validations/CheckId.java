package pl.kurs.vet.validations;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckIdValidator.class)
public @interface CheckId {

    String message() default "ID_NOT_FOUND";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends JpaRepository> repositoryType();
}
