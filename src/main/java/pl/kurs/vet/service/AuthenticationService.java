package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kurs.vet.model.security.AppRole;
import pl.kurs.vet.model.security.AppUser;
import pl.kurs.vet.repository.AppUserRepository;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    private final AppUserRepository repository;

    //@PostConstruct
    public void init() {
        AppUser appUser1 = new AppUser("admin", "$2a$10$GzMXuTJn39mqx5J6xk5.o.MVbrJaJn7zqv7lkAXpX4aUanQSImf7q", "admin@test.pl");
        AppUser appUser2 = new AppUser("test", "$2a$10$Ow.0rhmmvuNo.GMzCQr0F.36PPC7mVe0swUG657pBopJpuyKh6zT.", "test@test.pl");

        AppRole admin = new AppRole("ROLE_ADMIN");
        AppRole test = new AppRole("ROLE_TEST");

        appUser1.setRoles(Set.of(admin));
        appUser2.setRoles(Set.of(test));

        repository.saveAll(Arrays.asList(appUser1, appUser2));

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsernameWithRoles(username).orElseThrow();
    }
}
