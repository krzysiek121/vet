package pl.kurs.vet.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.jni.Local;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
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
import org.springframework.web.bind.MethodArgumentNotValidException;
import pl.kurs.vet.VetApplication;
import pl.kurs.vet.exception.NoEmptySlotsException;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.Patient;
import pl.kurs.vet.model.Visit;
import pl.kurs.vet.model.dto.CheckDto;
import pl.kurs.vet.model.dto.DoctorDtoCheck;
import pl.kurs.vet.repository.DoctorRepository;
import pl.kurs.vet.repository.PatientRepository;
import pl.kurs.vet.repository.VisitRepository;
import pl.kurs.vet.request.CreateCheckVisitCommand;
import pl.kurs.vet.request.CreateVisitCommand;
import pl.kurs.vet.response.ConfirmResponse;
import pl.kurs.vet.response.VisitSaveResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Test
    public void shouldAddVisitAndReturnCorrectId() throws Exception {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);


        String date = "2022-12-05 15:00";
        CreateVisitCommand toSave = new CreateVisitCommand(l1.getId(), p1.getId(), LocalDateTime.now());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        VisitSaveResponse id = objectMapper.readValue(response, VisitSaveResponse.class);
        Optional<Visit> visit1 = visitRepository.findById(id.getId());


        assertEquals(visit1.get().getDoctor().getId(), l1.getId());
        assertEquals(visit1.get().getPatient().getId(), p1.getId());
        assertEquals(visit1.get().getData(), toSave.getDate());
    }

    @Test
    public void shouldReturnCorrectTimeSlotsForVisits() throws Exception {

        //Doktor A - CHOMIKI: [zawaolne caly pon,wtorek,srodek,czwartek: 12-13, piatek,sob 10-11, niedz]
        //request: chomikow [wtorek a niedziela] 2 wizyty
        //

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);

        String date = "2022-12-10 12:00";

        LocalDateTime dateVisit = LocalDateTime.of(2022, 12, 10, 12, 00);

        CreateVisitCommand toSave = new CreateVisitCommand(1, 1, dateVisit);

        DoctorDtoCheck d1 = new DoctorDtoCheck(1, "Andrzej xx");
        CheckDto ch1 = new CheckDto(d1, "2022-12-10 12:00");
        CheckDto ch2 = new CheckDto(d1, "2022-12-10 13:00");
        CheckDto ch3 = new CheckDto(d1, "2022-12-10 14:00");
        CheckDto ch4 = new CheckDto(d1, "2022-12-10 15:00");
        List<CheckDto> visitsOptions = Arrays.asList(ch1, ch2, ch3, ch4);

        LocalDateTime start = LocalDateTime.of(2022, 12, 10, 12, 00);
        LocalDateTime stop = LocalDateTime.of(2022, 12, 10, 16, 00);

        CreateCheckVisitCommand c1 = new CreateCheckVisitCommand("kardiolog", "kot", start, stop);

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
        doctorRepository.saveAndFlush(l1);
        LocalDateTime time = LocalDateTime.of(2022, 12, 10, 12, 00);
        CreateVisitCommand toSave = new CreateVisitCommand(1, 1, time);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String id = result.getResponse().getContentAsString();

        VisitSaveResponse vr = objectMapper.readValue(id, VisitSaveResponse.class);

        Optional<Visit> visit1 = visitRepository.findById(vr.getId());


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

    @Test
    public void shouldReturnDoctorHaveVisitException() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);

        LocalDateTime time = LocalDateTime.of(2022, 12, 10, 12, 00);

        CreateVisitCommand toSave = new CreateVisitCommand(l1.getId(), p1.getId(), time);
        CreateVisitCommand toSave2 = new CreateVisitCommand(l1.getId(), p1.getId(), time);


        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("DOCTOR_HAS_VISIT_THAT_DATE"));

    }

    @Test
    public void shouldReturnVisitConfirmation() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);

        LocalDateTime time = LocalDateTime.of(2022, 12, 10, 12, 00);

        CreateVisitCommand toSave = new CreateVisitCommand(l1.getId(), p1.getId(), time);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        String id = result.getResponse().getContentAsString();
        Optional<Visit> visit1 = visitRepository.findById(Integer.valueOf(id.substring(id.indexOf(':', +1), id.indexOf('}')).substring(1)));

        ConfirmResponse c1 = new ConfirmResponse("VISIT_CONFIRMED");
        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/visit/confirm/{token}", visit1.get().getToken())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(objectMapper.writeValueAsString(c1))
                );


    }

    @Test
    public void shouldReturnCorrectTimeVisitSlots() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);
        LocalDateTime time = LocalDateTime.of(2022, 12, 10, 15, 00);
        List<LocalDateTime> slots = new ArrayList<>();
        slots.add(time);
        for (int i = 0; i < 65; i++) {
            visitRepository.saveAndFlush(new Visit(l1, p1, slots.get(slots.size() - 1).plusHours(1), "token", LocalDateTime.now()));

        }
    }

    @Test
    public void shouldReturnNoTypeDoctorException() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        doctorRepository.saveAndFlush(l1);

        LocalDateTime start = LocalDateTime.of(2022, 12, 10, 12, 00);
        LocalDateTime stop = LocalDateTime.of(2022, 12, 10, 16, 00);


        CreateCheckVisitCommand c1 = new CreateCheckVisitCommand(("laryngolog"), "kot", start, stop);


        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/check/")
                .content(objectMapper.writeValueAsString(c1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].message").value("DOCTOR_TYPE_NOT_SUPPORT"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();

    }

    @Test
    public void shouldReturnExceptionDateToEarlierThanDateFrom() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        doctorRepository.saveAndFlush(l1);

        LocalDateTime start = LocalDateTime.of(2022, 12, 10, 12, 00);
        LocalDateTime stop = LocalDateTime.of(2022, 12, 10, 16, 00);


        CreateCheckVisitCommand c1 = new CreateCheckVisitCommand(("kardiolog"), "kot", stop, start);


        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/check/")
                .content(objectMapper.writeValueAsString(c1))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].message").value("CHECK_INPUT_DATE_TO"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();

    }

    @Test
    public void shouldThrowFutureDateException() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);

        LocalDateTime date = LocalDateTime.of(2022, 01, 10, 16, 00);

        CreateVisitCommand toSave = new CreateVisitCommand(l1.getId(), p1.getId(), date);


        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("date"))
                .andExpect(jsonPath("$.[0].message").value("must be a date in the present or in the future"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();

    }

    @Test
    public void shouldThrowNoEmptySlotsException() throws Exception {


        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Patient p1 = new Patient("xx", "xx", "xxx", 11, "xx", "xxx", "wardawa.post@gmail.com");

        patientRepository.saveAndFlush(p1);
        doctorRepository.saveAndFlush(l1);

        LocalDateTime time = LocalDateTime.of(2022, 12, 10, 15, 00);


        List<LocalDateTime> slots = new ArrayList<>();
        slots.add(time);
        for (int i = 0; i < 200; i++) {

            if (i + 1 == 172 || i + 1 == 189) {
                visitRepository.saveAndFlush(new Visit(l1, p1, slots.get(slots.size() - 1).plusHours(2), "token" + i, LocalDateTime.now()));
                slots.add(slots.get(slots.size() - 1).plusHours(2));
            }
            visitRepository.saveAndFlush(new Visit(l1, p1, slots.get(slots.size() - 1).plusHours(1), "token" + i, LocalDateTime.now()));
            slots.add(slots.get(slots.size() - 1).plusHours(1));


        }

        DoctorDtoCheck d1 = new DoctorDtoCheck(1, "Andrzej xx");
        LocalDateTime start = LocalDateTime.of(2022, 12, 10, 18, 00);
        LocalDateTime stop = LocalDateTime.of(2022, 12, 18, 22, 00);

        CreateCheckVisitCommand c1 = new CreateCheckVisitCommand("kardiolog", "kot", start, stop);

        CheckDto ch1 = new CheckDto(d1, "2022-12-17 19:00");
        CheckDto ch2 = new CheckDto(d1, "2022-12-18 14:00");

        List<CheckDto> visitsOptions = Arrays.asList(ch1, ch2);

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/check/")
                .content(objectMapper.writeValueAsString(c1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content().json(objectMapper.writeValueAsString(visitsOptions)));


        LocalDateTime date1 = LocalDateTime.of(2022, 12, 17, 19, 00);
        LocalDateTime date2 = LocalDateTime.of(2022, 12, 18, 14, 00);


        CreateVisitCommand toSave = new CreateVisitCommand(l1.getId(), p1.getId(), date1);
        CreateVisitCommand toSave2 = new CreateVisitCommand(l1.getId(), p1.getId(), date2);

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/")
                .content(objectMapper.writeValueAsString(toSave2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());


        CreateCheckVisitCommand c2 = new CreateCheckVisitCommand("kardiolog", "kot", start, stop);


        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/visit/check/")
                .content(objectMapper.writeValueAsString(c2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("NO_EMPTY_SLOTS_IN_TIME_RANGE_CHANGE_TIME"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NoEmptySlotsException))
                .andReturn();

    }


}