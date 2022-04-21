package pl.kurs.vet.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vet.validations.CheckDate;
import pl.kurs.vet.validations.CheckDoctorId;
import pl.kurs.vet.validations.CheckPatientId;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVisitCommand {
    @NotNull(message = "doctorId_NOT_EMPTY")
    @CheckDoctorId
    private int doctorId;
    @NotNull(message = "patientId_NOT_EMPTY")
    @CheckPatientId
    private int patientId;
    @NotEmpty
    @CheckDate
    private String data;
}
