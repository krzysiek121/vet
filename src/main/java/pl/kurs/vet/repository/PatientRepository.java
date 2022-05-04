package pl.kurs.vet.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import pl.kurs.vet.model.Patient;

import javax.persistence.LockModeType;
import java.util.Optional;


public interface PatientRepository extends JpaRepository<Patient, Integer> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<Patient> findById(int id);
  boolean existsByEmail(String email);

}
