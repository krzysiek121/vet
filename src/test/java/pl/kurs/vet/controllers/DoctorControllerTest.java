package pl.kurs.vet.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.kurs.vet.VetApplication;
import pl.kurs.vet.model.Doctor;
import pl.kurs.vet.repository.DoctorReposirtory;
import pl.kurs.vet.service.DoctorService;

import java.io.UnsupportedEncodingException;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private DoctorReposirtory doctorReposirtory;

    @Autowired
    private DoctorService doctorService;

    @Test
    public void createDoctor() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");


        doctorReposirtory.saveAndFlush(l1);
//
        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/1/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Andrzej"))
                .andExpect(jsonPath("$.surname").value("xx"));

        //String str = result.getResponse().getContentAsString();
        //assertTrue(str.contains("\"name\":\"Andrzej\""));
    }
    @Test
    public void createDoctorSecoundTest() throws Exception {

        Doctor l1 = new Doctor("Andrzej", "xx", "kardiolog", "kot", 000, "xxx");
        //String json = objectMapper.writeValueAsString(l1);

        doctorReposirtory.saveAndFlush(l1);
//
        mockMvc.perform(MockMvcRequestBuilders
                .get("http://localhost:8080/doctor/")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}