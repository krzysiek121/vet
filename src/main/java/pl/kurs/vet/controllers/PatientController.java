package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.request.CreatePatientCommand;
import pl.kurs.vet.service.PatientService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@Scope("singleton")
public class PatientController {

    private final PatientRepository patientRepository;
    private final PatientService patientService;


    @PostMapping
    public ResponseEntity<Visit> save(CreatePatientCommand patientCommand) {
        Patient saved = patientService.save(patientCommand);
        return new ResponseEntity<Visit>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Patient> getById(@PathVariable(value = "id") int id) {
        return ResponseEntity.status(HttpStatus.OK).body(patientService.findPatientById(id));
    }

    @GetMapping
    public ResponseEntity<List<Patient>> findByPublished(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        return new ResponseEntity<List<Patient>>(patientService.patientListWithPagination(page, size), HttpStatus.OK);

    }
}
