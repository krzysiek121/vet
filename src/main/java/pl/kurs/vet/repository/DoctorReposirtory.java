package pl.kurs.vet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;

import javax.persistence.LockModeType;
import javax.print.Doc;
import java.util.List;

public interface DoctorReposirtory extends JpaRepository<Doctor, Integer> {
    @Transactional(readOnly = true)
    List<Doctor> findByType(String type);
    @Transactional(readOnly = true)
    List<Doctor> findByAnimalType(String type);
    boolean existsByNip(int nip);

}
