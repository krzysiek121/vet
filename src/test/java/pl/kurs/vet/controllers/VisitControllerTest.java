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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.VetApplication;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;
import pl.kurs.vet.model.dto.CheckDto;
import pl.kurs.vet.model.dto.DoctorDtoCheck;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.repository.VisitRepository;
import pl.kurs.vet.request.CreateCheckVisitCommand;
import pl.kurs.vet.request.CreateVisitCommand;
import pl.kurs.vet.response.ConfirmResponse;
import pl.kurs.vet.service.DoctorService;
import pl.kurs.vet.service.VisitService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
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


        assertEquals(visit1.get().getDoctor().getId(), l1.getId());


    }
    @Test
    public void shouldReturnCorrectTimeSlotsForVisits() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorReposirtory.saveAndFlush(l1);

        String date = "2022-12-10 12:00";
        CreateVisitCommand toSave = new CreateVisitCommand(1, 1, date);

        DoctorDtoCheck d1 = new DoctorDtoCheck(1, "Andrzej xx");
        CheckDto ch1 = new CheckDto(d1, "2022-12-10 13:00");
        CheckDto ch2 = new CheckDto(d1, "2022-12-10 14:00");
        CheckDto ch3 = new CheckDto(d1, "2022-12-10 15:00");
        List<CheckDto> visitsOptions = Arrays.asList(ch1, ch2, ch3);

        CreateCheckVisitCommand c1 = new CreateCheckVisitCommand("kardiolog", "kot","2022-12-10 12:00", "2022-12-10 16:00"  );

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/check/")
                .content(objectMapper.writeValueAsString(c1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                .content().json(objectMapper.writeValueAsString(visitsOptions)));

    }
    @Test
    public void shouldDeleteVisit() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorReposirtory.saveAndFlush(l1);

        String date = "2022-12-10 12:00";
        CreateVisitCommand toSave = new CreateVisitCommand(1, 1, date);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String id = result.getResponse().getContentAsString();
        Optional<Visit> visit1 = visitRepository.findById(Integer.valueOf(id.substring(id.indexOf(':', +1),id.indexOf('}') ).substring(1)));

        ConfirmResponse c1 = new ConfirmResponse("VISIT_CANCELED");
        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/visit/cancel/{token}", visit1.get().getToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(objectMapper.writeValueAsString(c1))
                );


    }
}