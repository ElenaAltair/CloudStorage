package ru.netology.cloudstorage.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.dto.JwtAuthResponseDto;
import ru.netology.cloudstorage.dto.UserAuthRequestDto;
import ru.netology.cloudstorage.exceptions.Exceptions;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repositories.AuthRepository;
import ru.netology.cloudstorage.security.CustomUserServiceImpl;
import ru.netology.cloudstorage.security.jwt.JwtService;

import javax.naming.AuthenticationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ru.netology.cloudstorage.log.Log.log;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Для хранения рефреш токена используется HashMap лишь для упрощения примера.
    // Лучше использовать какое-нибудь постоянное хранилище.
    private final Map<String, String> refreshStorage = new HashMap<>();


    public JwtAuthResponseDto login(UserAuthRequestDto userAuthRequestDto) throws AuthenticationException {

        // Получаем логин
        String login = userAuthRequestDto.getLogin();
        // Ищем пользователя в базе данных по логину
        Optional<User> optionalUserFromDatabase = authRepository.findUserByLogin(login);
        if (optionalUserFromDatabase.isPresent()) {
            User userFromDatabase = optionalUserFromDatabase.get();
            // проверяем пароль тот, что отправил пользователь, и тот что в базе данных у данного логина
            if (passwordEncoder.matches(userAuthRequestDto.getPassword(), userFromDatabase.getPassword())) {
                // генерируем accessToken и refreshToken
                JwtAuthResponseDto authResponse = jwtService.generateAuthToken(userFromDatabase);
                // Сохраняем логин и refreshToken в мапу refreshStorage
                refreshStorage.put(login, authResponse.getRefreshToken());
                log("INFO: Пользователь " + login + " вошёл в систему.", "", "");
                return authResponse;
            }
        }
        log("ERROR: Неправильный логин или пароль.", "", "");
        throw new Exceptions("Ошибка при введении данных.");
    }

    /*
    метод getAccessToken, принимает refresh токен, а возвращает новый access токен.
    Сначала мы проверяем, что присланный rehresh токен валиден.
    Если валиден, то получаем логин пользователя.
    Далее по логину находим выданный пользователю refresh токен в мапе refreshStorage,
    и сверяем его с присланным пользователем.
    Если токены одинаковые, то получаем объект userFromDatabase,
    далее генерируем новый access токен, без обновления refresh токена.
     */
    public JwtAuthResponseDto getAccessToken(String authToken) throws Exception {
        String refreshToken = authToken.substring(7);
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            // получаем логин
            String login = jwtService.getLoginFromToken(refreshToken);
            // находим токен по логину, который сохраниле в мапе refreshStorage
            String saveRefreshToken = refreshStorage.get(login);
            // сверяем его с присланным пользователем
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                // получаем пользователя из базы данных по логину
                final User userFromDatabase = authRepository.findUserByLogin(login).orElseThrow(() ->
                        new Exception(String.format("login %s not found", login)));
                // генерируем новый accessToken и refreshToken остаётся неизменным
                JwtAuthResponseDto authResponse = jwtService.refreshBaseToken(userFromDatabase, refreshToken);
                return authResponse;
            }
        }
        return new JwtAuthResponseDto(null, null);
    }


    public JwtAuthResponseDto refreshToken(String authToken) throws Exception {

        // извлекаем refreshToken
        String refreshToken = authToken.substring(7);
        if (refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            // получаем логин
            String login = jwtService.getLoginFromToken(refreshToken);
            // получаем пользователя из базы данных по логину
            User userFromDatabase = authRepository.findUserByLogin(login).orElseThrow(() ->
                    new Exception(String.format("Логин %s не найден.", login)));
            // генерируем новые accessToken и refreshToken
            JwtAuthResponseDto authResponse = jwtService.generateAuthToken(userFromDatabase);
            // Сохраняем логин и refreshToken в мапу refreshStorage
            refreshStorage.put(login, authResponse.getRefreshToken());

            return authResponse;
        }
        throw new Exceptions("Невалидный токен.");
    }

    public String logout() {

        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        refreshStorage.remove(login);
        return login;
    }


    public String addUser(User userNew) throws Exception {
        User user = userNew;
        // проверить на уникальность логина
        var userFromDatabase = authRepository.findUserByLogin(user.getLogin());
        if (userFromDatabase == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            authRepository.save(user);
            return "Пользователь добавлен";
        }
        return "Пользователь с таким логином уже существует. Добавление отменено.";

    }

}
