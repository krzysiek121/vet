package pl.kurs.vet.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePatientCommand {
    @NotEmpty(message = "NAME_OF_ANIMAL_NOT_EMPTY")
    private String nameOfAnimal;
    @NotEmpty(message = "SPECIES_NOT_EMPTY")
    private String species;
    @NotEmpty(message = "RACE_NOT_EMPTY")
    private String race;
    @NotNull(message = "AGE_NOT_EMPTY")
    @Range(min = 0)
    private Integer age;
    @NotEmpty(message = "OWNER_NAME_NOT_EMPTY")
    private String ownerName;
    @NotEmpty(message = "OWNER_SURNAME_NOT_EMPTY")
    private String ownerSurname;
    @NotEmpty(message = "EMAIL_NOT_EMPTY")
    private String email;
}
