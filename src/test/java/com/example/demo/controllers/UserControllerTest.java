package com.example.demo.controllers;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepositoryMock;

    @MockBean
    private CartRepository cartRepositoryMock;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoderMock;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreateUser() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("testuser");
        createUserRequest.setPassword("password123");
        createUserRequest.setConfirmPassword("password123");

        User mockedUser = new User();
        mockedUser.setId(0L);
        mockedUser.setUsername("testuser");

        Mockito.when(userRepositoryMock.save(any(User.class))).thenReturn(mockedUser);

        mockMvc.perform(post("/api/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(0))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testFindById() throws Exception {
        User mockedUser = new User();
        mockedUser.setId(1L);
        mockedUser.setUsername("testuser");

        Mockito.when(userRepositoryMock.findById(1L)).thenReturn(java.util.Optional.of(mockedUser));

        mockMvc.perform(get("/api/user/id/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    public void testFindByUserName() throws Exception {
        User mockedUser = new User();
        mockedUser.setId(1L);
        mockedUser.setUsername("testuser");

        Mockito.when(userRepositoryMock.findByUsername("testuser")).thenReturn(mockedUser);

        mockMvc.perform(get("/api/user/{username}", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // Helper method to convert an object to JSON string
    private String asJsonString(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
