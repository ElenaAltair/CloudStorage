package ru.netology.cloudstorage;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudStorageApplication {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(CloudStorageApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

    }

}
