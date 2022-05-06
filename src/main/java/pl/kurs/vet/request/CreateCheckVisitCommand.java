package pl.kurs.vet.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vet.validations.CheckAnimalSupport;
import pl.kurs.vet.validations.CheckDates;
import pl.kurs.vet.validations.CheckDoctorTypeSupport;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//example body: { "type": "kardiolog", "animal": "pies", "from": "date_from", "to": "date_to" }
@CheckDates
public class CreateCheckVisitCommand {
    @NotEmpty(message = "TYPE_NOT_EMPTY")
    @CheckDoctorTypeSupport
    private String type;
    @NotEmpty(message = "ANIMAL_NOT_EMPTY")
    @CheckAnimalSupport
    private String animal;
    @NotNull(message = "DATE_FROM_NOT_EMPTY")
    @FutureOrPresent
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime from;
    @NotNull(message = "DATE_TO_NOT_EMPTY")
    @FutureOrPresent
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime to;
}
