package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.vet.model.dto.PatientDtoGet;
import pl.kurs.vet.request.CreatePatientCommand;
import pl.kurs.vet.service.PatientService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@Scope("singleton")
public class PatientController {

    private final PatientService patientService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<PatientDtoGet> save(@RequestBody @Valid CreatePatientCommand patientCommand) {
        return new ResponseEntity<>(modelMapper.map(patientService.save(patientCommand), PatientDtoGet.class), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PatientDtoGet> getById(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(modelMapper.map(patientService.findPatientById(id), PatientDtoGet.class), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PatientDtoGet>> findByPublished(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(patientService.patientListWithPagination(page, size).stream()
                .map(p -> modelMapper.map(p, PatientDtoGet.class)).collect(Collectors.toList()), HttpStatus.OK);
    }
}
