package pl.kurs.vet.model;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Transactional
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull(message = "doctor_NOT_EMPTY")
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @NotNull(message = "patient_NOT_EMPTY")
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    private LocalDateTime data;
    private String token;
    private boolean isConfirmed = false;
    private LocalDateTime timeSendConfirmation;
    @Version
    private long version;

    public Visit(Doctor doctor, Patient patient, LocalDateTime data, String token, LocalDateTime timeSendConfirmation) {
        this.doctor = doctor;
        this.doctor.getVisits().add(this);
        this.patient = patient;
        this.patient.getVisits().add(this);
        this.data = data;
        this.token = token;
        this.timeSendConfirmation = timeSendConfirmation;
    }
}


