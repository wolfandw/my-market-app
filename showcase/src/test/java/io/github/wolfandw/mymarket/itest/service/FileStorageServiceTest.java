package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authorization.AuthorizationDeniedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.*;

/**
 * Модульный тест сервиса файлов.
 */
public class FileStorageServiceTest extends  AbstractIntegrationTest {
    private static final String FILE_NAME = "1.jpg";
    private static final int BUFFER_SIZE = 4096;

    private Path pathDir;
    private Path filePath;

    @BeforeEach
    protected void setUp() {
        pathDir = Paths.get(fileDir);
        filePath = pathDir.resolve(FILE_NAME);
    }

    @Test
    void readFileFileTest() {
        byte[] expectedContent = {1, 2, 3};
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
            mockPaths.when(() -> Paths.get(fileDir)).thenReturn(pathDir);
        }

        Path mockFilePath = Mockito.mock(Path.class);
        when(mockFilePath.resolve(FILE_NAME)).thenReturn(filePath);
        when(mockFilePath.normalize()).thenReturn(filePath);

        DataBuffer mockDataBuffer = Mockito.mock(DataBuffer.class);
        when(mockDataBuffer.readableByteCount()).thenReturn(3);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length == 1 && arguments[0] != null) {
                    byte[] c = (byte[]) arguments[0];
                    System.arraycopy(expectedContent, 0, c, 0, c.length);
                }
                return null;
            }
        }).when(mockDataBuffer).read(any());

        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            mockFiles.when(() -> Files.exists(pathDir)).thenReturn(true);
            mockFiles.when(() -> Files.exists(filePath)).thenReturn(true);
            mockFiles.when(() -> Files.readAllBytes(filePath)).thenReturn(expectedContent);

            try (MockedStatic<DataBufferUtils> mockFDataBufferUtils = Mockito.mockStatic(DataBufferUtils.class)) {
                mockFDataBufferUtils.when(() -> DataBufferUtils.read(any(Path.class), any(DataBufferFactory.class), any(Integer.class))).thenReturn(Flux.just(mockDataBuffer));
                mockFDataBufferUtils.when(() -> DataBufferUtils.join(any(Flux.class))).thenReturn(Mono.just(mockDataBuffer));

                StepVerifier.create(fileStorageService.readFile(FILE_NAME)).
                        consumeNextWith(actualContent -> {
                            assertArrayEquals(expectedContent, actualContent, "Содержимое файлов должно совпадать");
                        }).verifyComplete();
            }
        }
    }

    @Test
    @IsRoleAdmin
    void writeFileAdminTest() {
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
            mockPaths.when(() -> Paths.get(fileDir)).thenReturn(pathDir);
        }

        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            mockFiles.when(() -> Files.exists(pathDir)).thenReturn(true);
        }

        Path mockFilePath = Mockito.mock(Path.class);
        when(mockFilePath.resolve(FILE_NAME)).thenReturn(filePath);

        FilePart mockMultipartFile = Mockito.mock(FilePart.class);
        when(mockMultipartFile.transferTo(any(Path.class))).thenReturn(Mono.empty());

        StepVerifier.create(fileStorageService.writeFile(FILE_NAME, mockMultipartFile)).
                consumeNextWith(actualContent -> {
                    assertThat(actualContent).isEqualTo(FILE_NAME);
        }).verifyComplete();
    }

    @Test
    @IsRoleUser
    void writeFileUserTest() {
        FilePart mockMultipartFile = Mockito.mock(FilePart.class);
        StepVerifier.create(fileStorageService.writeFile(FILE_NAME, mockMultipartFile)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void writeFileTest() {
        FilePart mockMultipartFile = Mockito.mock(FilePart.class);
        StepVerifier.create(fileStorageService.writeFile(FILE_NAME, mockMultipartFile)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    void deleteFileAdminTest() {
        try (MockedStatic<Paths> mockPaths = Mockito.mockStatic(Paths.class)) {
            mockPaths.when(() -> Paths.get(fileDir)).thenReturn(pathDir);
        }

        Path mockFilePath = Mockito.mock(Path.class);
        when(mockFilePath.resolve(FILE_NAME)).thenReturn(filePath);
        when(mockFilePath.normalize()).thenReturn(filePath);

        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            mockFiles.when(() -> Files.exists(pathDir)).thenReturn(true);
            mockFiles.when(() -> Files.exists(filePath)).thenReturn(true);
            mockFiles.when(() -> Files.delete(filePath)).thenAnswer(Answers.RETURNS_DEFAULTS);

            StepVerifier.create(fileStorageService.deleteFile(FILE_NAME)).verifyComplete();
        }
    }

    @Test
    @IsRoleUser
    void deleteFileUserTest() {
        StepVerifier.create(fileStorageService.deleteFile(FILE_NAME)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void deleteFileTest() {
        StepVerifier.create(fileStorageService.deleteFile(FILE_NAME)).verifyError(AuthorizationDeniedException.class);
    }
}
