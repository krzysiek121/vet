package pl.kurs.vet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UC_DOCTOR_NIP", columnNames = "nip")
})
@EqualsAndHashCode(exclude = {"visits"})
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotEmpty(message = "NAME_NOT_EMPTY")
    private String name;
    @NotEmpty(message = "SURNAME_NOT_EMPTY")
    private String surname;
    @NotEmpty(message = "TYPE_NOT_EMPTY")
    private String type;
    @NotEmpty(message = "ANIMAL_NOT_EMPTY")
    private String animalType;
    @NotNull(message = "SALARY_NOT_EMPTY")
    private Integer salary;
    @NotEmpty(message = "NIP_NOT_EMPTY")
    private String nip;
    private boolean isWorking = true;
    @Version
    private long version;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = {CascadeType.ALL})
    private Set<Visit> visits = new HashSet<>();

    public Doctor(@NotEmpty(message = "NAME_NOT_EMPTY") String name, @NotEmpty(message = "SURNAME_NOT_EMPTY") String surname, @NotEmpty(message = "TYPE_NOT_EMPTY") String type, @NotEmpty(message = "ANIMAL_NOT_EMPTY") String animalType, @NotEmpty(message = "SALARY_NOT_EMPTY") Integer salary, @NotEmpty(message = "NIP_NOT_EMPTY") String nip) {
        this.name = name;
        this.surname = surname;
        this.type = type;
        this.animalType = animalType;
        this.salary = salary;
        this.nip = nip;
    }
}
