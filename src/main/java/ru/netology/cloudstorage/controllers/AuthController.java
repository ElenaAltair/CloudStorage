package ru.netology.cloudstorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.cloudstorage.dto.JwtAuthResponseDto;
import ru.netology.cloudstorage.dto.UserAuthRequestDto;
import ru.netology.cloudstorage.exceptions.ErrorResponse;
import ru.netology.cloudstorage.exceptions.Exceptions;
import ru.netology.cloudstorage.services.AuthService;
import javax.naming.AuthenticationException;
import static ru.netology.cloudstorage.log.Log.log;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class AuthController {

    private final AuthService authService;

    /*
    Аутентификация
    Схема аутентификации с использованием JWT:
    Пользователь вводит свои учетные данные.
    При успешной аутентификации сервис предоставляет пользователю токен, содержащий сведения об этом пользователе.
    */

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> login(@RequestBody UserAuthRequestDto authRequest) throws AuthenticationException {
        JwtAuthResponseDto authResponse = authService.login(authRequest);
        return ResponseEntity.ok(authResponse);
    }
    // тест
    // curl --json "{\"login\": \"Admin\", \"password\": \"admin\"}" http://localhost:8081/login


    @GetMapping("/hello")
    public String hello() {
        return "hello ";
    }

    // /token  принимает RefreshTokenDto c единственным полем refreshToken
    // и возвращает AuthResponse с новым access токеном
    // (refreshToken остаётся прежним)
    @PostMapping("/token")
    public ResponseEntity<JwtAuthResponseDto> getNewAccessToken(@RequestHeader("auth-token") String authToken) throws Exception {
        final JwtAuthResponseDto token = authService.getAccessToken(authToken);
        return ResponseEntity.ok(token);
    }

    // /refresh  принимает RefreshTokenDto c единственным полем refreshToken
    // и возвращает JwtAuthResponseDto с новыми токенами
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponseDto> getNewRefreshToken(@RequestHeader("auth-token") String authToken) throws Exception {

        final JwtAuthResponseDto token = authService.refreshToken(authToken);
        return ResponseEntity.ok(token);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exceptions.class)
    public ErrorResponse handleBadRequest(Exceptions e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleInternalServerError(RuntimeException e) {
        log("ERROR: Внутренняя ошибка сервера", "", "");
        return new ErrorResponse("ERROR: Внутренняя ошибка сервера");
    }

}
