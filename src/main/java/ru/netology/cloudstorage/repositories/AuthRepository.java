package ru.netology.cloudstorage.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.model.UserEntity;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
public class AuthRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public AuthRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> findUserByLogin(String login) {
        List<UserEntity> customersName = jdbcTemplate.query(
                "select u.id, u.username, u.password,  a.authority as authority\n" +
                        "from public.users u\n" +
                        "inner join public.authorities a on u.username = a.username\n" +
                        "where u.username = :login\n",
                new MapSqlParameterSource()
                        .addValue("login", login),
                (rs, rowNum) -> new UserEntity(
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("authority")
                ));

        User user = new User();
        user.setId(customersName.get(0).getId());
        user.setLogin(customersName.get(0).getLogin());
        user.setPassword(customersName.get(0).getPassword());
        Set<Role> set = new HashSet<>();
        for (UserEntity userEntity : customersName) {
            set.add(Role.valueOf(userEntity.getRole()));
        }
        user.setRoles(set);

        return Optional.ofNullable(user);
    }

    public void save(User user) {
        jdbcTemplate.update(
                "insert into public.users (username, password, enabled)\n" +
                        "values  ('" + user.getLogin() + "', '" + user.getPassword() + "', 1)",
                new MapSqlParameterSource());

        for (Role role : user.getRoles()) {
            jdbcTemplate.update(
                    "insert into public.authorities (username, authority)\n" +
                            "values  ('" + user.getLogin() + "', '" + role.getAuthority() + "')",
                    new MapSqlParameterSource());
        }
    }
}
