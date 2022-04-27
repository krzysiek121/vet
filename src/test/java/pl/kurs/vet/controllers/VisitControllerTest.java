package pl.kurs.vet.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.VetApplication;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.repository.VisitRepository;
import pl.kurs.vet.request.CreateVisitCommand;
import pl.kurs.vet.service.DoctorService;
import pl.kurs.vet.service.VisitService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {VetApplication.class})
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
@Transactional
class VisitControllerTest {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorReposirtory doctorReposirtory;

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private VisitService visitService;
    @Autowired
    private VisitRepository visitRepository;

    @Test
    public void shouldAddVisitAndReturnCorrectId() throws Exception {


        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorReposirtory.saveAndFlush(l1);

        String token = visitService.generateNewToken();
        String date = "2022-12-05 15:00";
        CreateVisitCommand toSave = new CreateVisitCommand(l1.getId(), p1.getId(), date);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String id = result.getResponse().getContentAsString();
        Optional<Visit> visit1 = visitRepository.findById(Integer.valueOf(id.substring(id.indexOf(':', +1),id.indexOf('}') ).substring(1)));
        //Optional<Visit> visit1 = visitRepository.findById(Integer.valueOf(id.substring(id.indexOf(':', +1), id.indexOf('}'))));


        assertEquals(visit1.get().getDoctor().getId(), l1.getId());


    }
}