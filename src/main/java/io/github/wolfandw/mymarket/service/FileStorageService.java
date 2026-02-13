package io.github.wolfandw.mymarket.service;

import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

/**
 * Сервис работы с файлами.
 */
public interface FileStorageService {
    /**
     * Возвращает содержимое файла.
     *
     * @param fileName имя файла
     * @return содержимое файла
     */
    Mono<byte[]> readFile(String fileName);

    /**
     * Сохраняет файл.
     *
     * @param fileName имя файла
     * @param file     сохраняемый файл
     * @return имя файла
     */
    Mono<String> writeFile(String fileName, FilePart file);

    /**
     * Удаляет файл.
     *
     * @param fileName имя файла
     */
    Mono<Void> deleteFile(String fileName);
}
