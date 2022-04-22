package pl.kurs.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;

import javax.persistence.LockModeType;
import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Integer> {



}
