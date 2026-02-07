//package io.github.wolfandw.mymarket.itest.service;
//
//import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Answers;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//import static org.junit.jupiter.api.Assertions.assertArrayEquals;
//import static org.mockito.Mockito.*;
//
///**
// * Модульный тест сервиса файлов.
// */
//public class FileStorageServiceTest extends  AbstractIntegrationTest {
//    private static final String FILE_NAME = "1.jpg";
//
//    private Path pathDir;
//    private Path filePath;
//
//    @BeforeEach
//    void setUp() {
//        pathDir = Paths.get(fileDir);
//        filePath = pathDir.resolve(FILE_NAME);
//    }
//
//    @Test
//    void readFileFileTest() throws IOException {
//        byte[] expectedContent = {1, 2, 3};
//        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
//            mockPaths.when(() -> Paths.get(fileDir)).thenReturn(pathDir);
//        }
//
//        Path mockFilePath = Mockito.mock(Path.class);
//        when(mockFilePath.resolve(FILE_NAME)).thenReturn(filePath);
//        when(mockFilePath.normalize()).thenReturn(filePath);
//
//        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
//            mockFiles.when(() -> Files.exists(pathDir)).thenReturn(true);
//            mockFiles.when(() -> Files.exists(filePath)).thenReturn(true);
//            mockFiles.when(() -> Files.readAllBytes(filePath)).thenReturn(expectedContent);
//
//            byte[] actualContent = fileStorageService.readFile(FILE_NAME);
//
//            assertArrayEquals(expectedContent, actualContent, "Содержимое файлов должно совпадать");
//        }
//    }
//
//    @Test
//    void writeFileTest() throws IOException {
//        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
//            mockPaths.when(() -> Paths.get(fileDir)).thenReturn(pathDir);
//        }
//
//        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
//            mockFiles.when(() -> Files.exists(pathDir)).thenReturn(true);
//        }
//
//        Path mockFilePath = Mockito.mock(Path.class);
//        when(mockFilePath.resolve(FILE_NAME)).thenReturn(filePath);
//
//        MultipartFile mockMultipartFile = Mockito.mock(MultipartFile.class);
//        doNothing().when(mockMultipartFile).transferTo(filePath);
//
//        fileStorageService.writeFile(FILE_NAME, mockMultipartFile);
//
//        verify(mockMultipartFile).transferTo(filePath);
//    }
//
//    @Test
//    void deleteFileTest() throws IOException {
//        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
//            mockPaths.when(() -> Paths.get(fileDir)).thenReturn(pathDir);
//        }
//
//        Path mockFilePath = Mockito.mock(Path.class);
//        when(mockFilePath.resolve(FILE_NAME)).thenReturn(filePath);
//        when(mockFilePath.normalize()).thenReturn(filePath);
//
//        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
//            mockFiles.when(() -> Files.exists(pathDir)).thenReturn(true);
//            mockFiles.when(() -> Files.exists(filePath)).thenReturn(true);
//            mockFiles.when(() -> Files.delete(filePath)).thenAnswer(Answers.RETURNS_DEFAULTS);
//
//            fileStorageService.deleteFile(FILE_NAME);
//            mockFiles.verify(() -> Files.delete(filePath));
//        }
//    }
//}
