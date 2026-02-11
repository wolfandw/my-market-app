package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест сервиса картинок.
 */
public class EntityImageServiceIntegrationTest extends AbstractIntegrationTest {
    private static final String PARAMETER_IMAGE_FILE = "imageFile";

    @BeforeEach
    public void setup() throws IOException {
        Path dest = Paths.get(fileDir);
        Path src = new ClassPathResource(fileDirTest).getFilePath();
        if (Files.exists(src) && Files.exists(dest)) {
            Stream<Path> files = Files.walk(src);
            files.forEach(file -> {
                try {
                    Files.copy(file, dest.resolve(src.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ignored) {
                }
            });
            files.close();
        }
    }

    @AfterEach
    void cleanUp() throws IOException {
        Path dest = Paths.get(fileDir);
        if (Files.exists(dest)) {
            Stream<Path> files = Files.walk(dest);
            files.forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException ignored) {
                }
            });
            files.close();
        }
    }

    @Test
    void getEntityImageTest() throws IOException {
        Long entityId = 5L;
        String imageName = entityId + ".png";

        StepVerifier.create(fileStorageService.readFile(imageName).zipWith(entityImageService.getEntityImage(entityId))).consumeNextWith (tuple -> {
                            byte[] expectedImageData = tuple.getT1();
                            EntityImageDto actualEntityImage = tuple.getT2();
                            assertNotNull(actualEntityImage, "Картинка должна быть получена");
                            assertArrayEquals(expectedImageData, actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
                            assertEquals(MediaType.IMAGE_PNG, actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
                            assertEquals(entityId, actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");
                }).verifyComplete();
    }

    @Test
    void getEntityImageBase64Test() throws IOException {
        Long entityId = 5L;

        String imageName = entityId + ".png";

        StepVerifier.create(fileStorageService.readFile(imageName).zipWith(entityImageService.getEntityImageBase64(entityId))).consumeNextWith (tuple -> {
            byte[] expectedImageData = tuple.getT1();
            String base64Encoded = new String(Base64.getEncoder().encode(expectedImageData), StandardCharsets.UTF_8);
            String expectedItemImageBase64 = "data:" + MediaType.IMAGE_PNG + ";base64, " + base64Encoded;

            String actualEntityImageBase64 = tuple.getT2();
            assertNotNull(actualEntityImageBase64, "Картинка должна быть получена");
            assertEquals(expectedItemImageBase64, actualEntityImageBase64, "Данные картинки должны быть равны исходным");
        }).verifyComplete();
    }

    @Test
    void updateEntityImageTest() throws IOException {
        Long entityId = 5L;
        String imageName = "14.jpg";

        Mono<FilePart> expectedFilePartMono = fileStorageService.readFile(imageName).map(expectedImageData -> new FilePart() {
            @Override
            public String name() {
                return "name";
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.EMPTY;
            }

            @Override
            public Flux<DataBuffer> content() {
                return DataBufferUtils.read(new ByteArrayResource(expectedImageData), new DefaultDataBufferFactory(), 4096);
            }

            @Override
            public String filename() {
                return imageName;
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return DataBufferUtils.write(this.content(), dest);
            }
        });

        trxStepVerifier.create(entityImageService.setEntityImage(entityId, expectedFilePartMono).then(entityImageService.getEntityImage(entityId).zipWith(fileStorageService.readFile(imageName))))
                .consumeNextWith (tuple -> {
                    EntityImageDto actualEntityImage = tuple.getT1();
                    byte[] expectedData = tuple.getT2();
                    assertNotNull(actualEntityImage, "Картинка должна быть получена");
                    assertArrayEquals(expectedData, actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
                    assertEquals(MediaType.IMAGE_JPEG, actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
                    assertEquals(entityId, actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");
        }).verifyComplete();
    }
}
