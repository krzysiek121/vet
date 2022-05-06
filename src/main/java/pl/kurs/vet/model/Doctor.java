package pl.kurs.vet.model;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
@Getter
@Setter
@NoArgsConstructor
@Entity
@EqualsAndHashCode(exclude = {"visits"})
@Transactional
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String surname;
    private String type;
    private String animalType;
    private Integer salary;
    private String nip;
    private boolean isWorking = true;
    @Version
    private long version;
    @ToString.Exclude
    @OneToMany(mappedBy = "doctor", cascade = {CascadeType.ALL})
    private Set<Visit> visits = new HashSet<>();


    public Doctor(String name,String surname,String type, String animalType, Integer salary,String nip) {
        this.name = name;
        this.surname = surname;
        this.type = type;
        this.animalType = animalType;
        this.salary = salary;
        this.nip = nip;

    }
}

