package ru.netology.cloudstorage.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.netology.cloudstorage.dto.JwtAuthResponseDto;
import ru.netology.cloudstorage.dto.UserAuthRequestDto;
import ru.netology.cloudstorage.services.AuthService;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private MockMvc mockMvc;
    private AuthService authService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(authService)).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void loginTest() throws Exception {

        UserAuthRequestDto userDto = new UserAuthRequestDto("Admin", "admin");

        JwtAuthResponseDto authResponseDto = new JwtAuthResponseDto();

        Mockito.when(authService.login(userDto)).thenReturn(authResponseDto);

        mockMvc.perform(post("/login")
                        .header("auth-token", "Bearer auth-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
