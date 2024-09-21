package ru.netology.cloudstorage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtAuthResponseDto { // AuthResponse
    private final String type = "Bearer";
    private String accessToken;
    @JsonProperty("auth-token")
    private String refreshToken;
}
