package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.dto.DoctorDtoGet;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.request.CreateDoctorCommand;
import pl.kurs.vet.service.DoctorService;
import pl.kurs.vet.validations.CheckDoctorId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Scope("singleton")
public class DoctorController {

    private final DoctorService doctorService;
    private final DoctorReposirtory doctorReposirtory;
    private final ModelMapper modelMapper;


    @PostMapping
    public ResponseEntity<Doctor> saveDoctor(@RequestBody CreateDoctorCommand command) {
        Doctor saved = doctorService.save(command);
        return new ResponseEntity<Doctor>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DoctorDtoGet> getById(@PathVariable(value = "id") int id) {
        return new ResponseEntity<DoctorDtoGet>(modelMapper.map(doctorService.findDoctorById(id), DoctorDtoGet.class), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DoctorDtoGet>> findBAllWithPagination(@RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<List<DoctorDtoGet>>(doctorService.doctorListWithPagination(page, size)
                .stream().map(s -> modelMapper.map(s, DoctorDtoGet.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    @Transactional
    @PutMapping(value = "/fire/{id}")
    public ResponseEntity<String> softDelete(@PathVariable(value = "id") int id) {
        Doctor doctor = doctorService.findDoctorById(id);
        doctor.setWorking(false);
        return new ResponseEntity<String>("changed status of given doctor, this doctor will not be able to handle any visits", HttpStatus.OK);

    }
}
