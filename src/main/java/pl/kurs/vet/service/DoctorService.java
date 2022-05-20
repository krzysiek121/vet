package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.exception.DoctorNotFoundException;
import pl.kurs.vet.exception.DoctorNotWorkingException;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.repository.DoctorRepository;
import pl.kurs.vet.request.CreateDoctorCommand;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;


    @Transactional
    public Doctor save(CreateDoctorCommand command) {
        Doctor toSave = new Doctor(command.getName(), command.getSurname(), command.getType(), command.getAnimalType(), command.getSalary(), command.getNip());
        return doctorRepository.saveAndFlush(toSave);
    }

    @Transactional(readOnly = true)
    public Doctor findDoctorById(int id) {
        return doctorRepository.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Doctor> doctorListWithPagination(int page, int size) {
        return doctorRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Transactional
    public void softDelete(int id) {
        Doctor doctor = findDoctorById(id);
        if (doctor.isWorking()) {
            doctor.setWorking(false);
        } else {
            throw new DoctorNotWorkingException("DOCTOR_HAVE_NOT_WORKING_STATUS");
        }

    }
}
