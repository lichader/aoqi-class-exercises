package com.diangezan.api.user;

import com.diangezan.api.user.db.DbUser;
import com.diangezan.api.user.db.UserRepository;
import com.diangezan.api.user.web.model.CreateUserRequestWebModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UsersController.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    public void createUser_Success200() throws Exception {
        var mockDbSaveResult = new DbUser();
        mockDbSaveResult.setId(1L);
        mockDbSaveResult.setUsername("ripeng");
        mockDbSaveResult.setPassword("password");
        mockDbSaveResult.setFirstName("Richard");
        mockDbSaveResult.setLastName("Peng");
        mockDbSaveResult.setEmail("ripeng@test.com");
        mockDbSaveResult.setMobile("041233212");
        Mockito.when(userRepository.save(any(DbUser.class))).thenReturn(mockDbSaveResult);

        Mockito.when(restTemplate.postForEntity(any(String.class), any(), any())).thenReturn(null);

        var userInRequest = new DbUser();
        userInRequest.setUsername("ripeng");
        userInRequest.setPassword("password");
        userInRequest.setFirstName("Richard");
        userInRequest.setLastName("Peng");
        userInRequest.setEmail("ripeng@test.com");
        userInRequest.setMobile("041233212");

        var request = new CreateUserRequestWebModel();
        request.setData(userInRequest);

        mockMvc.perform(post("/users")
                                            .content(asJsonString(request))
                                            .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Location", "/api/user/v1/users/1"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("ripeng"))
                .andExpect(jsonPath("$.data.password").isEmpty());

        verify(userRepository, times(1)).save(any(DbUser.class));
        verify(restTemplate, times(1)).postForEntity(any(String.class), any(), any());
    }

    private String asJsonString(Object request) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(request);
    }

    @Test
    public void createUser_Error400() throws Exception {
        var user = new DbUser();
        user.setUsername("ripeng");

        var request = new CreateUserRequestWebModel();
        request.setData(user);

        mockMvc.perform(post("/users")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Username or password must be provided!"));
    }


    @Test
    public void createUser_Error409() throws Exception {
        var dummyData = new DbUser();
        Mockito.when(userRepository.findByUsername("ripeng")).thenReturn(Optional.of(dummyData));

        var user = new DbUser();
        user.setUsername("ripeng");
        user.setPassword("password1234");

        var request = new CreateUserRequestWebModel();
        request.setData(user);

        mockMvc.perform(post("/users")
                .content(asJsonString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(status().reason("Username has already been used!"));
    }

    @Test
    public void getUserById_Success200() throws Exception {
        var user = new DbUser();
        user.setId(1L);
        user.setUsername("ripeng");
        user.setPassword("password");
        user.setFirstName("Richard");
        user.setLastName("Peng");
        user.setEmail("ripeng@test.com");
        user.setMobile("041233212");

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("ripeng"))
                .andExpect(jsonPath("$.data.password").isEmpty());

    }

    @Test
    public void getUserById_Error404() throws Exception {
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("User doesn't exist!"));
    }
}
