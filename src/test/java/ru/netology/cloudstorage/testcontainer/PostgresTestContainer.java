package ru.netology.cloudstorage.testcontainer;

import org.testcontainers.containers.PostgreSQLContainer;


public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {

    public static PostgreSQLContainer postgresContainer;

    public PostgresTestContainer() {
        super("postgres:14.3-alpine");
    }

    public static PostgreSQLContainer getInstance() {
        if (postgresContainer == null) {
            postgresContainer = new PostgresTestContainer().withDatabaseName("postgres");
        }
        return postgresContainer;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("jdbc:mysql://localhost:5433/postgres", postgresContainer.getJdbcUrl());
        System.setProperty("root", postgresContainer.getUsername());
        System.setProperty("root", postgresContainer.getPassword());
    }

    @Override
    public void stop() {
    }

}
