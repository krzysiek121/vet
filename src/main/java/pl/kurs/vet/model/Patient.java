package pl.kurs.vet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"visits"})
@Entity
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nameOfAnimal;
    private String species;
    private String race;
    private int age;
    private String ownerName;
    private String ownerSurname;
    private String email;
    @Version
    private long version;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner1", cascade = {CascadeType.ALL})
    private Set<Visit> visits = new HashSet<>();

    public Patient(String nameOfAnimal, String species, String race, int age, String ownerName, String ownerSurname, String email) {
        this.nameOfAnimal = nameOfAnimal;
        this.species = species;
        this.race = race;
        this.age = age;
        this.ownerName = ownerName;
        this.ownerSurname = ownerSurname;
        this.email = email;
    }
}
