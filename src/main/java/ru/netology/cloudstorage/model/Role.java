package ru.netology.cloudstorage.model;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

// enum отвечающий за роль пользователя
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {

    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    @Override
    public String getAuthority() {
        return value;
    }
}
