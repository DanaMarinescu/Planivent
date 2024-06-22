package com.event.eventapp.controller;

import com.event.eventapp.DTO.UserLoginDTO;
import com.event.eventapp.service.DefaultUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class LoginControllerTests {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private DefaultUserService userService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(username = "baduser@example.com", password = "badpassword")
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    @WithMockUser(username = "user@example.com", password = "password")
    public void testLoginUser_Successful() throws Exception {
        UserLoginDTO user = new UserLoginDTO();
        user.setUsername("user@example.com");
        user.setPassword("password");

        given(userService.loadUserByUsername("user@example.com")).willReturn(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>()));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilizator autentificat cu succes."));

        verify(userService).loadUserByUsername("user@example.com");
    }

    @Test
    @WithMockUser(username = "baduser@example.com", password = "badpassword")
    public void testLoginUser_Failure() throws Exception {
        UserLoginDTO user = new UserLoginDTO();
        user.setUsername("baduser@example.com");
        user.setPassword("badpassword");

        given(userService.loadUserByUsername("baduser@example.com")).willThrow(new RuntimeException("User not found"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"username\":\"baduser@example.com\",\"password\":\"badpassword\"}")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                .andExpect(content().encoding("UTF-8"))
                .andExpect(content().string("Email sau parolă greşite. Te rog, încearcă din nou."));

        verify(userService).loadUserByUsername("baduser@example.com");
    }
}
