package pl.kurs.vet.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.validations.CheckDate;
import pl.kurs.vet.validations.CheckDoctorId;
import pl.kurs.vet.validations.CheckId;
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
    @CheckId(repositoryType = DoctorReposirtory.class)
    private int doctorId;
    @NotNull(message = "patientId_NOT_EMPTY")
    @CheckId(repositoryType = PatientRepository.class)
    private int patientId;
    @NotEmpty
    @CheckDate
    private String data;
}
