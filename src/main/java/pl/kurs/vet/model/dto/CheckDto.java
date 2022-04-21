package pl.kurs.vet.model.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckDto {
    private DoctorDtoCheck doctor;
    private String data;

}
