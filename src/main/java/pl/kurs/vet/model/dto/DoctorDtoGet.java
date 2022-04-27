package pl.kurs.vet.model.dto;

import lombok.*;

//{"name": "xx", "surname": "xx", "type": "xxx", "animalType": "yyy", "salary": 000, "nip": "xxx"}
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
