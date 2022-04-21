package pl.kurs.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.model.Visit;

import javax.persistence.LockModeType;

public interface VisitRepository extends JpaRepository<Visit, Integer> {
    //@Lock(LockModeType.PESSIMISTIC_READ)
    //@Transactional(readOnly = true)
    //Visit findById(int id);
    //@Lock(LockModeType.PESSIMISTIC_READ)
    Visit findByToken(String token);


}
