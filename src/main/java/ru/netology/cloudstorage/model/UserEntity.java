package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEntity {
    private long id;
    private String login;
    private String password;
    private String role;
}
