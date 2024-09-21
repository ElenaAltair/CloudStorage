package ru.netology.cloudstorage.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {
    private long id;
    private String name;
    private String contentType;
    private Long size;
    private Instant data;
    private String user;
    private byte[] content;
}
