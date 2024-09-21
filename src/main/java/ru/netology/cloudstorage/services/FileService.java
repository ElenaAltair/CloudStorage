package ru.netology.cloudstorage.services;


import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileDto;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repositories.FileRepository;
import ru.netology.cloudstorage.security.CustomUserServiceImpl;
import ru.netology.cloudstorage.security.jwt.JwtFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final JwtFilter jwtFilter;

    public FileService(FileRepository fileRepository, JwtFilter jwtFilter) {
        this.fileRepository = fileRepository;
        this.jwtFilter = jwtFilter;
    }

    public List<FileDto> findAllFilesByLogin(String authToken, User user, int limit) {
        List<File> files = fileRepository.findAllFilesByLogin(user.getLogin()).get();

        List<FileDto> filesDto = new ArrayList<>();
        for (File file : files) {
            FileDto fileDto = new FileDto();
            fileDto.setFilename(file.getName());
            fileDto.setSize(file.getSize());
            filesDto.add(fileDto);
        }

        filesDto.stream()
                .limit(limit)
                .collect(Collectors.toList());

        return filesDto;
    }

    public void addFile(String authToken, MultipartFile fileNew, String fileName) throws IOException {

        File file = new File();

        String namePath1 = fileName.substring(0, fileName.lastIndexOf("."));
        String namePath2 = fileName.substring(namePath1.length(), fileName.length());

        file.setName(namePath1 + "_" + Instant.now() + namePath2);
        file.setContentType(fileNew.getContentType());
        file.setData(Instant.now());
        file.setSize(fileNew.getSize());
        file.setUser(CustomUserServiceImpl.getCurrentUser().getLogin());
        file.setContent(fileNew.getBytes());
        fileRepository.add(file);

    }

    public void renameFile(String authToken, String filename, String filenameNew) {

        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        //String namePath1 = filenameNew.substring(0, filenameNew.lastIndexOf("."));
        //String namePath2 = filenameNew.substring(namePath1.length(), filenameNew.length());
        //filenameNew = namePath1 + "_" + Instant.now() + namePath2;
        fileRepository.renameFile(filename, filenameNew, login);

    }

    public void deleteFile(String authToken, String filename) {

        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        fileRepository.deleteFile(filename, login);

    }


    public File downloadFile(String authToken, String filename) {

        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        return fileRepository.downloadFile(filename, login);

    }


}


