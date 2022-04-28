package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.request.CreatePatientCommand;
import pl.kurs.vet.service.PatientService;

import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@Scope("singleton")
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<Patient> save(CreatePatientCommand patientCommand) {
        return  ResponseEntity.status(HttpStatus.CREATED).body(patientService.save(patientCommand));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Patient> getById(@PathVariable(value = "id") int id) {
        return new ResponseEntity<Patient>(patientService.findPatientById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> findByPublished(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size
    ) { return new ResponseEntity<List<Patient>>(patientService.patientListWithPagination(page, size), HttpStatus.OK);
    }
}
