package ru.netology.cloudstorage.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.model.File;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FileRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public FileRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<List<File>> findAllFilesByLogin(String login) {

        List<File> files = jdbcTemplate.query(
                "select f.id, f.name, f.content_type, f.size, f.data, f.login, f.content " +
                        "from public.files f \n" +
                        "where f.login = :login ",
                new MapSqlParameterSource()
                        .addValue("login", login),
                (rs, rowNum) -> new File(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("content_type"),
                        rs.getLong("size"),
                        rs.getTimestamp("data").toInstant(),
                        rs.getString("login"),
                        rs.getBytes("content")
                ));

        return Optional.ofNullable(files);
    }

    public void add(File file) {

        String sql = "insert into public.files (name, content_type, size, data, login, content) " +
                "values('" + file.getName() + "', '" + file.getContentType() + "', '" + file.getSize() + "', '" + file.getData() + "', '" + file.getUser() + "', '" + file.getContent() + "');";

        jdbcTemplate.update(sql, new MapSqlParameterSource());

        //
        // а вот так почему-то не работает
        // нашла совет обновить spring core до версии   4.1.1.RELEASE
        // implementation("org.springframework:spring-core:4.1.1.RELEASE")
        // но я эту зависимость не использую, или я не поняла о чём речь
        //
        /*
        jdbcTemplate.update(
                "insert into cloudstorage.files (name, content_type, size, data, login) values(:name, :content_type, :size, :data, :login)",
                new MapSqlParameterSource()
                        .addValue("name", file.getName(), Types.VARCHAR)
                        .addValue("content_type", file.getContentType(), Types.VARCHAR)
                        .addValue("size", file.getSize(), Types.BIGINT)
                        .addValue("data", file.getData(), Types.TIMESTAMP)
                        .addValue("login", file.getUser(), Types.VARCHAR));
        */
    }


    public void renameFile(String filename, String newFilename, String login) {
        String sql = "update public.files set name = '" + newFilename + "' where name = '" + filename + "' and login = '" + login + "'";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
    }


    public void deleteFile(String filename, String login) {
        String sql = "delete from public.files where name = '" + filename + "' and login = '" + login + "'";
        jdbcTemplate.update(sql, new MapSqlParameterSource());
    }

    public File downloadFile(String filename, String login) {
        File file = jdbcTemplate.query(
                "select f.id, f.name, f.content_type, f.size, f.data, f.login, f.content " +
                        "from public.files f \n" +
                        "where f.name = :filename and f.login = :login ",
                new MapSqlParameterSource()
                        .addValue("filename", filename)
                        .addValue("login", login),
                (rs, rowNum) -> new File(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("content_type"),
                        rs.getLong("size"),
                        rs.getTimestamp("data").toInstant(),
                        rs.getString("login"),
                        rs.getBytes("content")
                )).get(0);
        return file;
    }
}
