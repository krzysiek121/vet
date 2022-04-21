package pl.kurs.vet.mappings;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.dto.DoctorDtoGet;

public class DoctorToDoctorDtoGetConverter implements Converter<Doctor, DoctorDtoGet> {

    @Override
    public DoctorDtoGet convert(MappingContext<Doctor, DoctorDtoGet> mappingContext) {
        Doctor doctor = mappingContext.getSource();
        return DoctorDtoGet.builder()
                .name(doctor.getName())
                .surname(doctor.getSurname())
                .type(doctor.getType())
                .animalType(doctor.getAnimalType())
                .salary(doctor.getSalary())
                .nip(doctor.getNip())
                .build();
    }
}

