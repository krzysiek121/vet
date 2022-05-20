package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.kurs.vet.model.Doctor;
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
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDtoGet saveDoctor(@RequestBody @Valid CreateDoctorCommand command) {
        return modelMapper.map(doctorService.save(command), DoctorDtoGet.class);

    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoctorDtoGet getById(@PathVariable(value = "id") int id) {
        return modelMapper.map(doctorService.findDoctorById(id), DoctorDtoGet.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<DoctorDtoGet> findBAllWithPagination(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "3") int size) {
        return doctorService.doctorListWithPagination(page, size).stream()
                .map(s -> modelMapper.map(s, DoctorDtoGet.class)).collect(Collectors.toList());
    }

    @PutMapping(value = "/{id}/fire")
    @ResponseStatus(HttpStatus.OK)
    public String softDelete(@PathVariable(value = "id") int id) {
        doctorService.softDelete(id);
        return "CHANGED_DOCTOR_STATUS";

    }
}
