package pl.kurs.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import pl.kurs.vet.model.Doctor;

@SpringBootApplication
public class VetApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetApplication.class, args);
    }


}
