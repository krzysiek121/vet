package pl.kurs.vet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vet.VetApplication;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.model.dto.DoctorDtoGet;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.service.DoctorService;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {VetApplication.class})
@TestPropertySource(
        locations = "classpath:application-integrationtest.properties")
@AutoConfigureMockMvc
@Transactional
class DoctorControllerTest {

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

    @Test
    public void shouldCreateDoctor() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");


        doctorReposirtory.saveAndFlush(l1);

//
        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/{id}", l1.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Andrzej"))
                .andExpect(jsonPath("$.surname").value("xx"))
                .andExpect(jsonPath("$.type").value("kardiolog"))
                .andExpect(jsonPath("$.animalType").value("kot"))
                .andExpect(jsonPath("$.salary").value(000))
                .andExpect(jsonPath("$.nip").value("xxx"));
        //String str = result.getResponse().getContentAsString();
        //assertTrue(str.contains("\"name\":\"Andrzej\""));
    }
    public void addDoctorsToDatabase() {

    }
    @Test
    public void createDoctorSecoundTest() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        //String json = objectMapper.writeValueAsString(l1);

        doctorReposirtory.saveAndFlush(l1);
//
        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/{id}", l1.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .json(objectMapper.writeValueAsString(l1))
                );

    }
    @Test
    public void shouldGetAddedDoctors() throws Exception {

        String obj1= "[{\"name\":\"Andrzej\",\"surname\":\"xx\",\"type\":\"kardiolog\",\"animalType\":\"kot\",\"salary\":0,\"nip\":\"xxx\"},{\"name\":\"Darek\",\"surname\":\"xx\",\"type\":\"kardiolog\",\"animalType\":\"pies\",\"salary\":0,\"nip\":\"xxxx\"},{\"name\":\"Mariusz\",\"surname\":\"xx\",\"type\":\"kardiolog\",\"animalType\":\"kot\",\"salary\":0,\"nip\":\"xxx1\"}]";

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Doctor l2 = new Doctor("Darek", "xx", "kardiolog", "pies", 000, "xxxx");
        Doctor l3 = new Doctor("Mariusz", "xx", "kardiolog", "kot", 000, "xxx1");

        doctorReposirtory.saveAndFlush(l1);
        doctorReposirtory.saveAndFlush(l2);
        doctorReposirtory.saveAndFlush(l3);

        List<Doctor> doctors2 = Arrays.asList(l1,l2,l3);
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
    public void shouldGetAddedDoctorsAndReturnListSize() throws Exception {

        String obj1= "[{\"name\":\"Andrzej\",\"surname\":\"xx\",\"type\":\"kardiolog\",\"animalType\":\"kot\",\"salary\":0,\"nip\":\"xxx\"},{\"name\":\"Darek\",\"surname\":\"xx\",\"type\":\"kardiolog\",\"animalType\":\"pies\",\"salary\":0,\"nip\":\"xxxx\"},{\"name\":\"Mariusz\",\"surname\":\"xx\",\"type\":\"kardiolog\",\"animalType\":\"kot\",\"salary\":0,\"nip\":\"xxx1\"}]";

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        Doctor l2 = new Doctor("Darek", "xx", "kardiolog", "pies", 000, "xxxx");
        Doctor l3 = new Doctor("Mariusz", "xx", "kardiolog", "kot", 000, "xxx1");

        doctorReposirtory.saveAndFlush(l1);
        doctorReposirtory.saveAndFlush(l2);
        doctorReposirtory.saveAndFlush(l3);

        List<Doctor> doctors2 = Arrays.asList(l1,l2,l3);
        System.out.println(objectMapper.writeValueAsString(doctors2));

        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(doctors2.size()));



    }
    @Test
    public void shouldGetCorrectStatusAfterAddDoctor() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");

        mockMvc.perform(MockMvcRequestBuilders.post("http://localhost:8080/doctor/")
                .content(objectMapper.writeValueAsString(l1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }
    @Test
    public void shouldFireDoctor() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");

        String message = "changed status of given doctor, this doctor will not be able to handle any visits";

        doctorReposirtory.saveAndFlush(l1);

        mockMvc.perform(MockMvcRequestBuilders.put("http://localhost:8080/doctor/{id}/fire", l1.getId())
                .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .content()
                        .string(message));




    }
}