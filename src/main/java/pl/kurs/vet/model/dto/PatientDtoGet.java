package pl.kurs.vet.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PatientDtoGet {

    private String nameOfAnimal;
    private String species;
    private String race;
    private int age;
    private String ownerName;
    private String ownerSurname;
    private String email;
}
