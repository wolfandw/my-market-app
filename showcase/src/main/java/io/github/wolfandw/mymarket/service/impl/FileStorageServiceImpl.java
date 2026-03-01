package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Реализация {@link FileStorageService}
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {
    private static final Logger LOG = LoggerFactory.getLogger(FileStorageServiceImpl.class);
    private static final int BUFFER_SIZE = 4096;
    private static final int MAX_BYTES = 5 * 1024 * 1024; // 5МБ

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
    public Mono<byte[]> readFile(String fileName) {
        Path pathDir = Paths.get(fileDir);
        if (Files.exists(pathDir)) {
            Path filePath = pathDir.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                Flux<DataBuffer> dataBufferFlux = DataBufferUtils.read(filePath, new DefaultDataBufferFactory(), BUFFER_SIZE);
                Mono<DataBuffer> dataBufferMono = DataBufferUtils.join(dataBufferFlux);
                return dataBufferMono.map(dataBuffer -> {
                    try {
                        int readable = dataBuffer.readableByteCount();
                        if (readable > 0 && readable <= MAX_BYTES) {
                            byte[] content = new byte[readable];
                            dataBuffer.read(content);
                            return content;
                        }
                    } finally {
                        DataBufferUtils.release(dataBuffer);
                    }
                    return new byte[0];
                });
            }
            return Mono.just(new byte[0]);
        }
        return Mono.empty();
    }

    @Override
    public Mono<String> writeFile(String fileName, FilePart file) {
        Path pathDir = Paths.get(fileDir);
        if (!Files.exists(pathDir)) {
            try {
                Files.createDirectories(pathDir);
            } catch (IOException e) {
                LOG.error("Файл не записан {}", fileName, e);
            }
        }
        Path filePath = pathDir.resolve(fileName);
        return file.transferTo(filePath).thenReturn(fileName);
    }

    @Override
    public Mono<Void> deleteFile(String fileName) {
        Path pathDir = Paths.get(fileDir);
        if (Files.exists(pathDir)) {
            Path filePath = pathDir.resolve(fileName).normalize();
            if (Files.exists(filePath)) {
                return Mono.fromRunnable(() -> {
                    try {
                        Files.delete(filePath);
                    } catch (IOException e) {
                        LOG.error("Файл не удален {}", fileName, e);
                    }
                }).then();
            }
        }
        return Mono.empty();
    }
}
