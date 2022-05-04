package pl.kurs.vet.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import pl.kurs.vet.validations.CheckUniqueNip;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorCommand {

    @NotEmpty(message = "NAME_NOT_EMPTY")
    private String name;
    @NotEmpty(message = "SURNAME_NOT_EMPTY")
    private String surname;
    @NotEmpty(message = "TYPE_NOT_EMPTY")
    private String type;
    @NotEmpty(message = "ANIMAL_NOT_EMPTY")
    private String animalType;
    @NotNull(message = "SALARY_NOT_EMPTY")
    @Range( min = 1 )
    private Integer salary;
    @NotEmpty(message = "NIP_NOT_EMPTY")
    @CheckUniqueNip
    private String nip;

}
