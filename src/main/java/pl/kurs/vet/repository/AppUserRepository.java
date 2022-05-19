package pl.kurs.vet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.kurs.vet.model.security.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    @Query("select u from AppUser u left join fetch u.roles where u.username = ?1")
    Optional<AppUser> findByUsernameWithRoles(String username);

}
