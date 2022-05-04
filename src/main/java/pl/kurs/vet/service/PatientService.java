package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.exception.PatientNotFoundException;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.request.CreatePatientCommand;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;


    public Patient save(CreatePatientCommand command) {
        Patient toSave = new Patient(command.getNameOfAnimal(), command.getSpecies(), command.getRace(),
                command.getAge(), command.getOwnerName(), command.getOwnerSurname(), command.getEmail());
        return patientRepository.saveAndFlush(toSave);
    }
    @Transactional(readOnly = true)
    public Patient findPatientById(int id) {
        return patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));
    }
    @Transactional(readOnly = true)
    public List<Patient> patientListWithPagination(int page, int size) {
        List<Patient> patients;
        Pageable paging = PageRequest.of(page, size);

        Page<Patient> pageTuts = patientRepository.findAll(paging);

        return patients = pageTuts.getContent();
    }
}
