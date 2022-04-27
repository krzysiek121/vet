package pl.kurs.vet.validations;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
@Service
@RequiredArgsConstructor
public class CheckIdValidator implements ConstraintValidator<CheckId, Integer> {

    private Class<? extends JpaRepository> repositoryType;
    private final ApplicationContext springApplicationContext;


    @Override
    public void initialize(CheckId constraintAnnotation) {
        this.repositoryType = constraintAnnotation.repositoryType();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        JpaRepository repo = springApplicationContext.getBean(repositoryType);
        return repo.existsById(integer);
    }
}
