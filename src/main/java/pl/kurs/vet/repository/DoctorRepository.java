package pl.kurs.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import pl.kurs.vet.model.Doctor;
import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;


@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Doctor> findById(int id);
    List<Doctor> findByType(String type);
    List<Doctor> findByAnimalType(String type);
    boolean existsByNip(String nip);
    List<Doctor> findByTypeAndAndAnimalType(String type, String animalType);
    boolean existsByAnimalType(String string);
    boolean existsByType(String string);

}
