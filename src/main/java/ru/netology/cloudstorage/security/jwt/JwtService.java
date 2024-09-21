package ru.netology.cloudstorage.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.netology.cloudstorage.dto.JwtAuthResponseDto;
import ru.netology.cloudstorage.model.User;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;


@Component
public class JwtService {

    private static final Logger LOGGER = LogManager.getLogger(JwtService.class);
    @Value("qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==")
    private String jwtSecret;

    // генерация access токена и refresh токена
    public JwtAuthResponseDto generateAuthToken(User user) {
        JwtAuthResponseDto jwtDto = new JwtAuthResponseDto();
        jwtDto.setAccessToken(generateJwtToken(user));
        jwtDto.setRefreshToken(generateRefreshToken(user));
        return jwtDto;
    }

    // метод обновления access токена
    public JwtAuthResponseDto refreshBaseToken(User user, String refreshToken) {
        JwtAuthResponseDto jwtDto = new JwtAuthResponseDto();
        jwtDto.setAccessToken(generateJwtToken(user));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    // метод извлечения логина (email) из токена
    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSingInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // проверка токена на валидность
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSingInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException expEx) {
            LOGGER.error("Срок действия токена истек (Token expired)", expEx);
        } catch (UnsupportedJwtException ujEx) {
            LOGGER.error("Форма токена неподдерживается jwt (Unsupported jwt)", ujEx);
        } catch (MalformedJwtException mjEx) {
            LOGGER.error("Форма токена некорректна для jwt (Malformed jwt)", mjEx);
        } catch (SecurityException secEx) {
            LOGGER.error("Недействительная подпись (Invalid signature)", secEx);
        } catch (Exception e) {
            LOGGER.error("Недопустимый токен (Invalid token)", e);
        }
        return false;
    }

    // генерация токена
    public String generateJwtToken(@NonNull User user) throws IllegalArgumentException {
        // Определяем сколько по времени будут валиден токен (в данном случае 1 минуту)
        Date date = Date.from(LocalDateTime.now().plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(user.getLogin())
                .expiration(date)
                .signWith(getSingInKey()) // алгоритм шифрования
                .claim("roles", user.getRoles()) // и наши произвольные claims: роли
                .compact();

    }


    // Метод generateRefreshToken делает все тоже самое, что и в generateToken,
    // только мы не передаем туда claims и указываем большее время жизни
    public String generateRefreshToken(@NonNull User user) {

        // Определяем сколько по времени будут валиден токен (в данном случае 1 день)
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .subject(user.getLogin())
                .expiration(date)
                .signWith(getSingInKey()) // алгоритм шифрования
                .compact();
    }


    private SecretKey getSingInKey() { // алгоритм шифрования
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

// мы передаем секретные ключи, для подписи и валидации токенов
    /*
    С помощью аннотации @Value Spring подставляет значение из файла application.properties.
    Поэтому нужно записать в application.properties значения ключей в формате Base64:
    jwt.secret.access=qBTmv4oXFFR2GwjexDJ4t6fsIUIUhhXqlktXjXdkcyygs8nPVEwMfo29VDRRepYDVV5IkIxBMzr7OEHXEHd37w==
    jwt.secret.refresh=zL1HB3Pch05Avfynovxrf/kpF9O2m4NCWKJUjEp27s9J2jEG3ifiKCGylaZ8fDeoONSTJP/wAzKawB8F9rOMNg==

    Как получить ключи?
    Самым надежным способом будет воспользоваться методом Keys.secretKeyFor(),
    который генерирует надежные ключи, но он возвращает объект SecretKey.
    Нам же нужно как-то получить текстовую строку, чтобы использовать ее в application.properties.
    Для этого можно получить массив байт ключа, используя метод  SecretKey.getEncoded(),
    и преобразовать их в Base64. Можно просто запустить этот класс и получить два ключа.
    Внимание, в конструкторе JWTToken, происходит обратный процесс.
    Мы преобразуем Base64 обратно в массив байт, после чего используем Keys.hmacShaKeyFor(),
    чтобы восстановить из этих байтов объект ключа SecretKey.
    смотри  метод getSingInKey()

    /*
    JWT токен (JSON Web Token)
    JWT состоит из трех основных частей: заголовка (header), нагрузки (payload) и подписи (signature)

    Header
    Заголовок является служебной частью и состоит из двух полей: типа токена, в данном случае JWT,
    и алгоритма хэширования подписи
    Для подписи JWT могут применяться и алгоритмы асимметричного шифрования, например RS256 (RSA-SHA256).
    Стандарт допускает использование и других алгоритмов, включая HS512, RS512, ES256, ES512, none и др.

    Payload
    Полезна нагрузка — это любые данные, которые вы хотите передать в токене.
    Стандарт предусматривает несколько зарезервированных полей:
    iss — (issuer) издатель токена
    sub — (subject) “тема”, назначение токена
    aud — (audience) аудитория, получатели токена
    exp — (expire time) срок действия токена
    nbf — (not before) срок, до которого токен не действителен
    iat — (issued at) время создания токена
    jti — (JWT id) идентификатор токена
    (Все эти claims не являются обязательными)
    (Payload не шифруется при использовании токена, поэтому не стоит передавать в нем чувствительные данные)

    Signature
    Подпись генерируется следующим образом:
    Закодированные заголовок и полезная нагрузка объединяются с точкой (".") в качестве разделителя.
    Затем эта строка хешируется указанным в header алгоритмом.
    Результат работы алгоритма хеширования и есть подпись.
    Другие сервисы знают пароль, которым авторизационный сервис подписывает токены.
    Или у них есть публичный ключ, которым они могут проверить подпись.
    Официальный сайт jwt.io предлагает два алгоритма хэширования: HS256 и RS256.
    Но можно использовать любой алгоритм с приватным ключом.
    */

/*
Refresh tokens
В современных схемах аутентификации, основанных на JWT, после прохождения аутентификации пользователь получает два токена:

access token — JWT, на основе которого приложение идентифицирует и авторизует пользователя;
refresh token — токен произвольного формата, служащий для обновления access token.
Access token при таком подходе имеет сильно ограниченное время жизни (например, одну минуту).
Refresh token же имеет длительное время жизни (день, неделя, месяц),
но он одноразовый и служит исключительно для обновления access token пользователя.

Схема аутентификации в таком случае выглядит следующим образом:

пользователь проходит процедуру аутентификации и получает от сервера access token и refresh token;
при обращении к ресурсу пользователь передает в запросе свой access token,
на основе которого сервер идентифицирует и авторизует клиента;
при истечении access token клиент передает в запросе свой refresh token
и получает от сервера новые access token и refresh token;
при истечении refresh token пользователь заново проходит процедуру аутентификации.
*/

/*
Пример того, как может выглядеть механизм аутентификации:

Клиент API, чаще всего это фронт, присылает нам объект с логином и паролем.
Если пароль подходит, то мы генерируем access и refresh токены и отправляем их в ответ.
Клиент API использует access токен для взаимодействия с остальным нашим эндпойнтами нашего API.
Через пять минут, когда access токен протухает, фронт отправляет refresh токен и получает новый access токен.
И так по кругу, пока не протухнет refresh токен.
Refresh токен выдается на 30 дней.
Примерно на 25-29 день клиент API отправляет валидный refresh токен вместе с валидным access токеном
и взамен получает новую пару токенов.
 */