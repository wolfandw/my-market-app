package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.mymarket.service.EntityImageService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест сервиса {@link EntityImageService}
 */
public class EntityImageServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private EntityImageService entityImageService;

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L, 0L, -1L})
    void getEntityImageTest(Long entityId) {
        EntityImageDto entityImage = entityImageService.getEntityImage(entityId);
        assertNotNull(entityImage, "Картинка поста не должна быть нулл");
        assertEquals(entityId, entityImage.getEntityId(), "Картинка должна содержать идентификатор поста");
        //assertArrayEquals(new byte[0], entityImage.getData(), "Данные картинки должны совпадать");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L, 0L, -1L})
    void updateEntityImageTest(Long entityId) {
        MockMultipartFile image = new MockMultipartFile("image",
                "image-file-name.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3});
        entityImageService.updateEntityImage(entityId, image);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L, 0L, -1L})
    void deletePostImageTest(Long entityId) {
        entityImageService.deleteEntityImage(entityId);
    }
}
