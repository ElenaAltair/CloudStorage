package ru.netology.cloudstorage.testcontainer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.cloudstorage.CloudStorageApplication;
import ru.netology.cloudstorage.dto.UserAuthRequestDto;

@Testcontainers
@SpringBootTest(classes = CloudStorageApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloudStorageTestContainer {

    private static final int PORT = 8081;
    private static final String LOGIN = "Admin";
    private static final String PASSWORD = "admin";


    public TestRestTemplate restTemplate;
    private UserAuthRequestDto userDto;

    public CloudStorageTestContainer() {
        this.restTemplate = new TestRestTemplate();
    }

    @Container
    public static PostgreSQLContainer postgreSQLContainer = PostgresTestContainer.getInstance();

    @Container
    public static GenericContainer<?> appBackendCloud =
            new GenericContainer<>("mycloudstorage:1.0")
                    .withExposedPorts(PORT)
                    .dependsOn(postgreSQLContainer);

    @BeforeEach
    public void init() {
        userDto = new UserAuthRequestDto(LOGIN, PASSWORD);
    }

    @Test
    void loginAppTest() {
        String getLoginURI = "http://" + appBackendCloud.getHost() + ":" + PORT + "/login";
        String authToken = restTemplate.postForObject(getLoginURI, userDto, String.class);

        System.out.println(authToken);
        Assertions.assertNotNull(authToken);
    }


}
