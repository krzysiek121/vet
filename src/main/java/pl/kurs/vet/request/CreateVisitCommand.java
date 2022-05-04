package pl.kurs.vet.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.kurs.vet.repository.DoctorRepository;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.validations.CheckDate;
import pl.kurs.vet.validations.CheckId;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVisitCommand {
    @NotNull(message = "doctorId_NOT_EMPTY")
    @CheckId(repositoryType = DoctorRepository.class)
    private int doctorId;
    @NotNull(message = "patientId_NOT_EMPTY")
    @CheckId(repositoryType = PatientRepository.class)
    private int patientId;
    @FutureOrPresent
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private LocalDateTime date;
}
