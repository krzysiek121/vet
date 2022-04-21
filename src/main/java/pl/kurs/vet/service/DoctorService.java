package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.exception.DoctorNotFoundException;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.request.CreateDoctorCommand;

import javax.annotation.PostConstruct;
import javax.persistence.LockModeType;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorReposirtory doctorReposirtory;


    @PostConstruct
    public void init() {
        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Doctor l2 = new Doctor("Darek", "xx", "kardiolog", "pies", 000, "xxxx");
        Doctor l3 = new Doctor("Mariusz", "xx", "kardiolog", "kot", 000, "xxx1");
        Doctor l4 = new Doctor("Adam", "xx", "kardiolog", "pies", 000, "xxx2");
        Doctor l5 = new Doctor("Krzysiek", "xx", "kardiolog", "kot", 000, "xxx3");
        Doctor l6 = new Doctor("Dawid", "xx", "kardiolog", "pies", 000, "xxx5");
        Doctor l7 = new Doctor("Eugeniusz", "xx", "kardiolog", "kot", 000, "xxx34");
        Doctor l8 = new Doctor("Witold", "xx", "kardiolog", "pies", 000, "xxx23");
        Doctor l9 = new Doctor("Staszek", "xx", "urolog", "kot", 000, "xxx32");
        Doctor l10 = new Doctor("Tomasz", "xx", "laryngolog", "pies", 000, "xxx31");
        Doctor l11 = new Doctor("Marcin", "xx", "laryngolog", "kot", 000, "xxx54");
        doctorReposirtory.saveAndFlush(l1);
        doctorReposirtory.saveAndFlush(l2);
        doctorReposirtory.saveAndFlush(l3);
        doctorReposirtory.saveAndFlush(l4);
        doctorReposirtory.saveAndFlush(l5);
        doctorReposirtory.saveAndFlush(l6);
        doctorReposirtory.saveAndFlush(l7);
        doctorReposirtory.saveAndFlush(l8);
        doctorReposirtory.saveAndFlush(l9);
        doctorReposirtory.saveAndFlush(l10);
        doctorReposirtory.saveAndFlush(l11);



    }

    public Doctor save(CreateDoctorCommand command) {
        Doctor toSave = new Doctor(command.getName(), command.getSurname(), command.getType(), command.getAnimalType(), command.getSalary(), command.getNip());
        return doctorReposirtory.saveAndFlush(toSave);
    }
    @Transactional(readOnly = true)
    @Lock(LockModeType.PESSIMISTIC_READ)
    public Doctor findDoctorById(int id) {
         return doctorReposirtory.findById(id).orElseThrow(() -> new DoctorNotFoundException(id));
    }
    @Transactional(readOnly = true)
    public List<Doctor> findAllDoctors() {
        return doctorReposirtory.findAll();
    }
    public List<Doctor> doctorListWithPagination(int page, int size) {

        List<Doctor> doctors = new ArrayList<Doctor>();
        Pageable paging = PageRequest.of(page, size);

        Page<Doctor> pageTuts = doctorReposirtory.findAll(paging);
        doctors = pageTuts.getContent();

        return doctors;
    }

 }