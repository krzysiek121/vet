package pl.kurs.vet.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Service;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.dto.DoctorDtoCheck;

@Service
public class DoctorToDoctorDtoCheckConverter implements Converter<Doctor, DoctorDtoCheck> {

    @Override
    public DoctorDtoCheck convert(MappingContext<Doctor, DoctorDtoCheck> mappingContext) {
        Doctor doctor = mappingContext.getSource();
        return DoctorDtoCheck.builder()
                .id(doctor.getId())
                .name(doctor.getName() + " " + doctor.getSurname())
                .build();
    }
}

