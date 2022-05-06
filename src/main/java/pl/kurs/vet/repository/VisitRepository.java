package pl.kurs.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.model.Visit;

import javax.persistence.LockModeType;
import java.util.Optional;
@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Visit> findByToken(String token);
    void deleteById(int id);


}
