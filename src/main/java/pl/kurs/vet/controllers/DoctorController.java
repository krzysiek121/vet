package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.kurs.vet.model.dto.DoctorDtoGet;
import pl.kurs.vet.request.CreateDoctorCommand;
import pl.kurs.vet.service.DoctorService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    public ResponseEntity<DoctorDtoGet> saveDoctor(@RequestBody @Valid CreateDoctorCommand command) {
        return new ResponseEntity<>(modelMapper.map(doctorService.save(command), DoctorDtoGet.class), HttpStatus.CREATED);

    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DoctorDtoGet> getById(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>(modelMapper.map(doctorService.findDoctorById(id), DoctorDtoGet.class), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<DoctorDtoGet>> findBAllWithPagination(@RequestParam(defaultValue = "0") int page,
     @RequestParam(defaultValue = "3") int size) {
        return new ResponseEntity<>(doctorService.doctorListWithPagination(page, size).stream()
                .map(s -> modelMapper.map(s, DoctorDtoGet.class)).collect(Collectors.toList()), HttpStatus.OK);
    }


    @PutMapping(value = "/{id}/fire")
    public ResponseEntity<String> softDelete(@PathVariable(value = "id") int id) {
        return new ResponseEntity<>("changed status of given doctor, this doctor will not be able to handle any visits", HttpStatus.OK);

    }
}
