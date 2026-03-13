package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authorization.AuthorizationDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест сервиса картинок.
 */
public class EntityImageServiceIntegrationTest extends AbstractIntegrationTest {
    @BeforeEach
    protected void setup() {
        setupImages();
    }

    @AfterEach
    protected void cleanUp() {
        cleanUpImages();
    }

    @Test
    void getEntityImageTest() {
        Long entityId = 5L;
        String imageName = entityId + ".png";

        trxStepVerifier.create(fileStorageService.readFile(imageName).zipWith(entityImageService.getEntityImage(entityId))).consumeNextWith(tuple -> {
            byte[] expectedImageData = tuple.getT1();
            EntityImageDto actualEntityImage = tuple.getT2();
            assertNotNull(actualEntityImage, "Картинка должна быть получена");
            assertArrayEquals(expectedImageData, actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
            assertEquals(MediaType.IMAGE_PNG, actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
            assertEquals(entityId, actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");
        }).verifyComplete();
    }

    @Test
    void getEntityImageBase64Test() {
        Long entityId = 5L;

        String imageName = entityId + ".png";

        trxStepVerifier.create(fileStorageService.readFile(imageName).zipWith(entityImageService.getEntityImageBase64(entityId))).consumeNextWith(tuple -> {
            byte[] expectedImageData = tuple.getT1();
            String base64Encoded = new String(Base64.getEncoder().encode(expectedImageData), StandardCharsets.UTF_8);
            String expectedItemImageBase64 = "data:" + MediaType.IMAGE_PNG + ";base64, " + base64Encoded;

            String actualEntityImageBase64 = tuple.getT2();
            assertNotNull(actualEntityImageBase64, "Картинка должна быть получена");
            assertEquals(expectedItemImageBase64, actualEntityImageBase64, "Данные картинки должны быть равны исходным");
        }).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    void setEntityImageAdminTest() {
        Long entityId = 5L;
        String imageName = "14.jpg";

        Mono<FilePart> expectedFilePartMono = getFilePart(imageName);

        trxStepVerifier.create(entityImageService.setEntityImage(entityId, expectedFilePartMono).then(entityImageService.getEntityImage(entityId).zipWith(fileStorageService.readFile(imageName))))
                .consumeNextWith(tuple -> {
                    EntityImageDto actualEntityImage = tuple.getT1();
                    byte[] expectedData = tuple.getT2();
                    assertNotNull(actualEntityImage, "Картинка должна быть получена");
                    assertArrayEquals(expectedData, actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
                    assertEquals(MediaType.IMAGE_JPEG, actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
                    assertEquals(entityId, actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");
                }).verifyComplete();
    }

    @Test
    @IsRoleUser
    void setEntityImageUserTest() {
        StepVerifier.create(entityImageService.setEntityImage(5L, Mono.empty())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void setEntityImageTest() {
        StepVerifier.create(entityImageService.setEntityImage(5L, Mono.empty())).verifyError(AuthorizationDeniedException.class);
    }
}
