package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.exception.PatientNotFoundException;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.request.CreateDoctorCommand;
import pl.kurs.vet.request.CreatePatientCommand;

import javax.annotation.PostConstruct;
import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    //@PostConstruct
    public void init() {
        Patient l1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");
        patientRepository.saveAndFlush(l1);
   }
    //imie zwierzecia, gatunek, rase, wiek, imie i nazwisko wlasciciela, email.
    public Patient save(CreatePatientCommand command) {
        Patient toSave = new Patient(command.getNameOfAnimal(), command.getSpecies(), command.getRace(),
                command.getAge(), command.getOwnerName(), command.getOwnerSurname(), command.getEmail());
        return patientRepository.saveAndFlush(toSave);
    }
    @Transactional(readOnly = true)
    //@Lock(LockModeType.PESSIMISTIC_READ)
    public Patient findPatientById(int id) {
        return patientRepository.findById(id).get();
    }
    public List<Patient> patientListWithPagination(int page, int size) {
        List<Patient> patients = new ArrayList<Patient>();
        Pageable paging = PageRequest.of(page, size);

        Page<Patient> pageTuts = patientRepository.findAll(paging);
        patients = pageTuts.getContent();
        return patients;
    }
}
