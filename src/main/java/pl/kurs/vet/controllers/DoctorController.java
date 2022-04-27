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

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Scope("singleton")
public class DoctorController {

    private final DoctorService doctorService;
    private final ModelMapper modelMapper;


    @PostMapping
    public ResponseEntity<Doctor> saveDoctor(@RequestBody CreateDoctorCommand command) {
        return  ResponseEntity.status(HttpStatus.CREATED).body(doctorService.save(command));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable(value = "id") int id) {
        return new ResponseEntity<Doctor>(doctorService.findDoctorById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Doctor>> findBAllWithPagination(@RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<List<Doctor>>(doctorService.doctorListWithPagination(page, size), HttpStatus.OK);
        //return new ResponseEntity<List<DoctorDtoGet>>(doctorService.doctorListWithPagination(page, size)
           //     .stream().map(s -> modelMapper.map(s, DoctorDtoGet.class)).collect(Collectors.toList()), HttpStatus.OK);
    }


    @PutMapping(value = "/{id}/fire")
    public ResponseEntity<String> softDelete(@PathVariable(value = "id") int id) {
        return new ResponseEntity<String>("changed status of given doctor, this doctor will not be able to handle any visits", HttpStatus.OK);

    }
}
