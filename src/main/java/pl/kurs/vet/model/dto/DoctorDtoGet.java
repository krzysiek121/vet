package pl.kurs.vet.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class DoctorDtoGet {

    private String name;
    private String surname;
    private String type;
    private String animalType;
    private int salary;
    private String nip;

}
