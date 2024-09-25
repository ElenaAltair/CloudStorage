package ru.netology.cloudstorage.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netology.cloudstorage.dto.JwtAuthResponseDto;
import ru.netology.cloudstorage.dto.UserAuthRequestDto;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repositories.AuthRepository;
import ru.netology.cloudstorage.security.jwt.JwtService;

import javax.naming.AuthenticationException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@SpringBootTest()
public class AuthServiceTest {
    private static final String LOGIN = "Admin";
    private static final String PASSWORD = "admin";
    @Autowired
    private AuthService authService;
    @MockBean
    private AuthRepository authRepository;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtAuthResponseDto jwtAuthResponseDto;
    @MockBean
    private User userEntity;
    @MockBean
    private UserAuthRequestDto userDto;

    @BeforeEach
    public void init() {

        Set<Role> roles = new HashSet();
        roles.add(Role.ADMIN);
        userDto = new UserAuthRequestDto(LOGIN, PASSWORD);
        userEntity = new User(1L, LOGIN, PASSWORD, roles);
    }

    @Test
    public void loginTest() throws AuthenticationException {
        jwtAuthResponseDto = new JwtAuthResponseDto();
        System.out.println(jwtAuthResponseDto);

        Mockito.when(authRepository.findUserByLogin(userDto.getLogin())).thenReturn(Optional.ofNullable(userEntity));
        Mockito.when(passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())).thenReturn(true);
        Mockito.when(jwtService.generateAuthToken(userEntity)).thenReturn(jwtAuthResponseDto);

        JwtAuthResponseDto jwtAuthResponseDto = authService.login(userDto);
        System.out.println(jwtAuthResponseDto);
        Assertions.assertNotNull(jwtAuthResponseDto);
    }


}
