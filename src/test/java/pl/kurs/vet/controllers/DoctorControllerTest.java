package pl.kurs.vet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.MethodArgumentNotValidException;
import pl.kurs.vet.VetApplication;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.dto.DoctorDtoGet;
import pl.kurs.vet.repository.DoctorRepository;
import pl.kurs.vet.request.CreateDoctorCommand;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {VetApplication.class})
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void shouldGetDoctor() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 11, "xxx");


        doctorRepository.saveAndFlush(l1);


        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/{id}", l1.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Andrzej"))
                .andExpect(jsonPath("$.surname").value("xx"))
                .andExpect(jsonPath("$.type").value("kardiolog"))
                .andExpect(jsonPath("$.animalType").value("kot"))
                .andExpect(jsonPath("$.salary").value(11))
                .andExpect(jsonPath("$.nip").value("xxx"));

    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void createDoctorSecondTest() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");
        //String json = objectMapper.writeValueAsString(l1);
        DoctorDtoGet l2 = new DoctorDtoGet("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");
        doctorRepository.saveAndFlush(l1);

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/{id}", l1.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(objectMapper.writeValueAsString(l2))
                );

    }
    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void shouldGetAddedDoctors() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");
        Doctor l2 = new Doctor("Darek", "xx", "kardiolog", "pies", 2, "xxxx");
        Doctor l3 = new Doctor("Mariusz", "xx", "kardiolog", "kot", 2, "xxx1");

        doctorRepository.saveAndFlush(l1);
        doctorRepository.saveAndFlush(l2);
        doctorRepository.saveAndFlush(l3);

        DoctorDtoGet l4 = new DoctorDtoGet("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");
        DoctorDtoGet l5 = new DoctorDtoGet("Darek", "xx", "kardiolog", "pies", 2, "xxxx");
        DoctorDtoGet l6 = new DoctorDtoGet("Mariusz", "xx", "kardiolog", "kot", 2, "xxx1");


        List<DoctorDtoGet> doctors2 = Arrays.asList(l4,l5,l6);
        System.out.println(objectMapper.writeValueAsString(doctors2));

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(objectMapper.writeValueAsString(doctors2))
                );


    }
    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void shouldGetAddedDoctorsAndReturnListSize() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");
        Doctor l2 = new Doctor("Darek", "xx", "kardiolog", "pies", 2, "xxxx");
        Doctor l3 = new Doctor("Mariusz", "xx", "kardiolog", "kot", 2, "xxx1");

        doctorRepository.saveAndFlush(l1);
        doctorRepository.saveAndFlush(l2);
        doctorRepository.saveAndFlush(l3);

        DoctorDtoGet l4 = new DoctorDtoGet("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");
        DoctorDtoGet l5 = new DoctorDtoGet("Darek", "xx", "kardiolog", "pies", 2, "xxxx");
        DoctorDtoGet l6 = new DoctorDtoGet("Mariusz", "xx", "kardiolog", "kot", 2, "xxx1");


        List<DoctorDtoGet> doctors2 = Arrays.asList(l4,l5,l6);
        System.out.println(objectMapper.writeValueAsString(doctors2));

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(doctors2.size()));



    }
    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void shouldGetCorrectStatusAfterAddDoctor() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/doctor/")
                .content(objectMapper.writeValueAsString(l1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }
    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    public void shouldFireDoctor() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");

        String message = "CHANGED_DOCTOR_STATUS";

        doctorRepository.saveAndFlush(l1);

        mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/doctor/{id}/fire", l1.getId())
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(message));




    }

    @Test
    public void shouldReturnNotUniqueNipException() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");

        doctorRepository.save(l1);

        CreateDoctorCommand l2 = new CreateDoctorCommand("Andrzej", "xx", "kardiolog", "kot", 2, "xxx");

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/doctor/")
                .content(objectMapper.writeValueAsString(l2))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.[0].field").value("nip"))
                .andExpect(jsonPath("$.[0].message").value("NIP_NOT_UNIQUE"))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andReturn();




    }
}