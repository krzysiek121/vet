package pl.kurs.vet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull(message = "doctor_NOT_EMPTY")
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor owner;
    @NotNull(message = "patient_NOT_EMPTY")
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient owner1;
    private LocalDateTime data;
    private String token;
    private boolean isConfirmed = false;
    private LocalDateTime timeSendConfirmation;
    @Version
    private long version;

    public Visit(Doctor owner, Patient owner1, LocalDateTime data, String token, LocalDateTime timeSendConfirmation) {
        this.owner = owner;
        this.owner1 = owner1;
        this.data = data;
        this.token = token;
        this.timeSendConfirmation = timeSendConfirmation;
    }
}


