package ru.netology.cloudstorage.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.FileDto;
import ru.netology.cloudstorage.exceptions.ErrorResponse;
import ru.netology.cloudstorage.exceptions.Exceptions;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.security.CustomUserServiceImpl;
import ru.netology.cloudstorage.services.FileService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ru.netology.cloudstorage.log.Log.log;

@RequiredArgsConstructor
@CrossOrigin
@RestController
public class FileController {

    private final FileService fileService;


    @GetMapping("/list")
    public ResponseEntity<List<FileDto>> listFile(@RequestHeader("auth-token") String authToken,
                                                  @RequestParam("limit") int limit
    ) {
            List<FileDto> files = fileService.findAllFilesByLogin(authToken, CustomUserServiceImpl.getCurrentUser(), limit);
            log("INFO: Список файлов для пользователя " + CustomUserServiceImpl.getCurrentUser().getLogin() + " выведен на экран.", " Class: FileService ", " Method: listFile");
            return ResponseEntity.ok(files);
    }

    @PostMapping("/file")
    public ResponseEntity<String> addFile(@RequestHeader("auth-token") String authToken,
                                          @NotNull @RequestParam("file") MultipartFile multipartFile,
                                          @RequestParam("filename") String fileName) {
        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        try {
            fileService.addFile(authToken, multipartFile, fileName);
            log("INFO: Файл " + fileName + " успешно добавлен в базу пользователем " + login + ".", " Class: FileService ", " Method: addFile");
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("Файл успешно добавлен: %s", multipartFile.getOriginalFilename()));
        } catch (Exception e) {
            log("ERROR: У пользователя " + login + " не получило добавить в базу в базу файл " + fileName + ".", " Class: FileService ", " Method: addFile");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Не получается загрузить файл: %s!", multipartFile.getOriginalFilename()));
        }
    }

    @PutMapping("/file")
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody Map<String, String> fileNameRequest) throws IOException {
        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        fileService.renameFile(authToken, filename, fileNameRequest.get("filename"));
        log ("INFO: Пользователь " + login + " переименовал файл " + filename, " Class: FileService ", " Method: renameFile");
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping("/file")
    public ResponseEntity<?> deleteFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename) {
        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        try {

            fileService.deleteFile(authToken, filename);
            log("INFO: Файл " + filename + " удалён пользователем " + login + ".", " Class: FileService ", " Method: deleteFile" );
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            log("ERROR: У " + login + " пользователя не получается удалить файл: " + filename + ".", " Class: FileService ", " Method: deleteFile");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("У пользователя не получается удалить файл: %s!", filename));
        }
    }

    // скачать файл
    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") String authToken,
                                               @RequestParam("filename") String filename) {
        String login = CustomUserServiceImpl.getCurrentUser().getLogin();
        File file = fileService.downloadFile(authToken, filename);

        log("INFO: Файл " + filename + " скачен пользователем " + login +".", " Class: FileService ", " Method: downloadFile");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .body(file.getContent());
    }



    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exceptions.class)
    public ErrorResponse handleBadRequest(Exceptions e) {
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleInternalServerError(RuntimeException e) {
        log("ERROR: Внутренняя ошибка сервера", "", "");
        return new ErrorResponse("Внутренняя ошибка сервера");
    }

}
