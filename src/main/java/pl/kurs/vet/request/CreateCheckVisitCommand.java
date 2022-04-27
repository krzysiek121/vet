package pl.kurs.vet.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vet.validations.CheckAnimalSupport;
import pl.kurs.vet.validations.CheckDate;
import pl.kurs.vet.validations.CheckDoctorTypeSupport;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//example body: { "type": "kardiolog", "animal": "pies", "from": "date_from", "to": "date_to" }
public class CreateCheckVisitCommand {
    @NotEmpty(message = "TYPE_NOT_EMPTY")
    @CheckDoctorTypeSupport
    private String type;
    @NotEmpty(message = "ANIMAL_NOT_EMPTY")
    @CheckAnimalSupport
    private String animal;
    @NotEmpty(message = "DATE_FROM_NOT_EMPTY")
    @CheckDate
    private String date_from;
    @NotEmpty(message = "DATE_TO_NOT_EMPTY")
    @CheckDate
    private String date_to;
}
