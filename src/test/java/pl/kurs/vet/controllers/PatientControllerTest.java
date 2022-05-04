package pl.kurs.vet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.kurs.vet.VetApplication;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.dto.DoctorDtoGet;
import pl.kurs.vet.model.dto.PatientDtoGet;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.service.PatientService;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {VetApplication.class})
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientService patientService;

    @Test
    public void createPatient() throws Exception {

        Patient l1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");
        patientRepository.saveAndFlush(l1);

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/patient/{id}/", l1.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nameOfAnimal").value(l1.getNameOfAnimal()))
                .andExpect(jsonPath("$.species").value(l1.getSpecies()))
                .andExpect(jsonPath("$.race").value(l1.getRace()))
                .andExpect(jsonPath("$.age").value(l1.getAge()))
                .andExpect(jsonPath("$.ownerName").value(l1.getOwnerName()))
                .andExpect(jsonPath("$.ownerSurname").value(l1.getOwnerSurname()));

    }

    @Test
    public void createPatientSecoundTest() throws Exception {

        Patient l1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");
        patientRepository.saveAndFlush(l1);

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/parient/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

    }
    @Test
    public void shouldReturnCorrectSizeOfList() throws Exception {

        Patient p1 = new Patient("xx","jamnik", "jamnik", 18,"Adam","Nowak", "wardawa.post@gmail.com");
        Patient p2 = new Patient("xx","jamnik", "jamnik", 18,"Adam","Nowak", "wardawa.post@gmail.com");
        Patient p3 = new Patient("xx","jamnik", "jamnik", 18,"Adam","Nowak", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        patientRepository.saveAndFlush(p2);
        patientRepository.saveAndFlush(p3);

        PatientDtoGet p4 = new PatientDtoGet("xx","jamnik", "jamnik", 18,"Adam","Nowak", "wardawa.post@gmail.com");
        PatientDtoGet p5 = new PatientDtoGet("xx","jamnik", "jamnik", 18,"Adam","Nowak", "wardawa.post@gmail.com");
        PatientDtoGet p6 = new PatientDtoGet("xx","jamnik", "jamnik", 18,"Adam","Nowak", "wardawa.post@gmail.com");

        List<PatientDtoGet> patients = Arrays.asList(p4,p5,p6);
        System.out.println(objectMapper.writeValueAsString(patients));

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/patient/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(patients.size()));


    }

}