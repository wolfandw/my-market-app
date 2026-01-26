package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

/**
 * Модульный тест сервиса файлов.
 */
public class FileStorageServiceTest extends AbstractIntegrationTest {
    private static final String FILE_DIR = "upload/images/";
    private static final Path PATH_DIR = Paths.get(FILE_DIR);
    private static final String FILE_NAME = "1.jpg";
    private static final Path FILE_PATH = PATH_DIR.resolve(FILE_NAME);

    @Test
    void readFileFileTest() throws IOException {
        byte[] expectedContent = {1, 2, 3};
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
            mockPaths.when(() -> Paths.get(FILE_DIR)).thenReturn(PATH_DIR);
        }

        Path mockFilePath = Mockito.mock(Path.class);
        when(mockFilePath.resolve(FILE_NAME)).thenReturn(FILE_PATH);
        when(mockFilePath.normalize()).thenReturn(FILE_PATH);

        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            mockFiles.when(() -> Files.exists(PATH_DIR)).thenReturn(true);
            mockFiles.when(() -> Files.exists(FILE_PATH)).thenReturn(true);
            mockFiles.when(() -> Files.readAllBytes(FILE_PATH)).thenReturn(expectedContent);

            byte[] actualContent = fileStorageService.readFile(FILE_NAME);

            assertArrayEquals(expectedContent, actualContent, "Содержимое файлов должно совпадать");
        }
    }

    @Test
    void writeFileTest() throws IOException {
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
            mockPaths.when(() -> Paths.get(FILE_DIR)).thenReturn(PATH_DIR);
        }

        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            mockFiles.when(() -> Files.exists(PATH_DIR)).thenReturn(true);
        }

        Path mockFilePath = Mockito.mock(Path.class);
        when(mockFilePath.resolve(FILE_NAME)).thenReturn(FILE_PATH);

        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
        doNothing().when(mockMultipartFile).transferTo(FILE_PATH);

        fileStorageService.writeFile(FILE_NAME, mockMultipartFile);

        verify(mockMultipartFile).transferTo(FILE_PATH);
    }

    @Test
    void deleteFileTest() throws IOException {
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
            mockPaths.when(() -> Paths.get(FILE_DIR)).thenReturn(PATH_DIR);
        }

        Path mockFilePath = Mockito.mock(Path.class);
        when(mockFilePath.resolve(FILE_NAME)).thenReturn(FILE_PATH);
        when(mockFilePath.normalize()).thenReturn(FILE_PATH);

        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            mockFiles.when(() -> Files.exists(PATH_DIR)).thenReturn(true);
            mockFiles.when(() -> Files.exists(FILE_PATH)).thenReturn(true);
            mockFiles.when(() -> Files.delete(FILE_PATH)).thenAnswer(Answers.RETURNS_DEFAULTS);

            fileStorageService.deleteFile(FILE_NAME);
            mockFiles.verify(() -> Files.delete(FILE_PATH));
        }
    }
}
