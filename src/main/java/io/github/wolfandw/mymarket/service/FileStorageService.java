package io.github.wolfandw.mymarket.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Сервис работы с файлами.
 */
public interface FileStorageService {
    /**
     * Возвращает содержимое файла.
     *
     * @param fileName имя файла
     * @return содержимое файла
     * @throws IOException I/O exception
     */
    byte[] readFile(String fileName) throws IOException;

    /**
     * Сохраняет файл.
     *
     * @param fileName имя файла
     * @param file     сохраняемый файл
     * @throws IOException I/O exception
     */
    void writeFile(String fileName, MultipartFile file) throws IOException;

    /**
     * Удаляет файл.
     *
     * @param fileName имя файла
     * @throws IOException I/O exception
     */
    void deleteFile(String fileName) throws IOException;
}
