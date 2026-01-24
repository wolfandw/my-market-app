package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Реализация {@link FileStorageService}
 */
@Service("fileStorageService")
public class FileStorageServiceImpl implements FileStorageService {
    private final String fileDir;

    /**
     * Создает сервис для работы с файлами.
     *
     * @param fileDir каталог файлов
     */
    public FileStorageServiceImpl(@Value("${mymarket.upload.images.dir}") String fileDir) {
        this.fileDir = fileDir;
    }

    @Override
    public byte[] readFile(String fileName) throws IOException {
        Path pathDir = Paths.get(fileDir);
        if (Files.exists(pathDir)) {
            Path filePath = pathDir.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                return Files.readAllBytes(filePath);
            }
        }
        return new byte[0];
    }

    @Override
    public void writeFile(String fileName, MultipartFile file) throws IOException {
        Path pathDir = Paths.get(fileDir);
        if (!Files.exists(pathDir)) {
            Files.createDirectories(pathDir);
        }
        Path filePath = pathDir.resolve(fileName);
        file.transferTo(filePath);
    }

    @Override
    public void deleteFile(String fileName) throws IOException {
        Path pathDir = Paths.get(fileDir);
        if (Files.exists(pathDir)) {
            Path filePath = pathDir.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        }
    }
}
