package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест сервиса картинок.
 */
public class EntityImageServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    void getEntityImageTest() throws IOException {
        Long entityId = 5L;

        Optional<Item> entity = itemRepository.findById(entityId);
        assertTrue(entity.isPresent(), "Сущность должна присутствовать");

        String imageName = entity.get().getImgPath();
        assertTrue(imageName.startsWith(entityId + "."), "Имя картинки должно начинаться с ид сущности");

        byte[] expectedImageData = fileStorageService.readFile(imageName);
        EntityImageDto expectedItemImage = new EntityImageDto(entityId, expectedImageData, MediaType.IMAGE_PNG);

        EntityImageDto actualEntityImage = entityImageService.getEntityImage(entityId);

        assertNotNull(actualEntityImage, "Картинка должна быть получена");
        assertArrayEquals(expectedItemImage.getData(), actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
        assertEquals(expectedItemImage.getMediaType(), actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
        assertEquals(expectedItemImage.getEntityId(), actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");
    }

    @Test
    @Transactional
    void updateEntityImageTest() throws IOException {
        Long entityId = 5L;

        Optional<Item> entity = itemRepository.findById(entityId);
        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
        String oldImagePath = entity.get().getImgPath();
        EntityImageDto oldEntityImage = entityImageService.getEntityImage(entityId);

        String imageName = "14.jpg";
        byte[] expectedImageData = fileStorageService.readFile(imageName);
        EntityImageDto expectedItemImage = new EntityImageDto(entityId, expectedImageData, MediaType.IMAGE_JPEG);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "image",
                imageName,
                MediaType.IMAGE_JPEG_VALUE,
                expectedImageData);

        entityImageService.updateEntityImage(entityId, multipartFile);

        EntityImageDto actualEntityImage = entityImageService.getEntityImage(entityId);

        assertNotNull(actualEntityImage, "Картинка должна быть получена");
        assertArrayEquals(expectedItemImage.getData(), actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
        assertEquals(expectedItemImage.getMediaType(), actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
        assertEquals(expectedItemImage.getEntityId(), actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");

        entityImageService.updateEntityImage(entityId, new MockMultipartFile(
                "image",
                oldImagePath,
                oldEntityImage.getMediaType().toString(),
                oldEntityImage.getData()));
    }

    @Test
    @Transactional
    void deleteItemImageTest() {
        Long entityId = 5L;

        Optional<Item> entity = itemRepository.findById(entityId);
        assertTrue(entity.isPresent(), "Сущность должна присутствовать");
        String oldImagePath = entity.get().getImgPath();
        EntityImageDto oldEntityImage = entityImageService.getEntityImage(entityId);

        EntityImageDto expectedItemImage = new EntityImageDto(entityId, new byte[0], MediaType.APPLICATION_OCTET_STREAM);

        entityImageService.deleteEntityImage(entityId);
        EntityImageDto actualEntityImage = entityImageService.getEntityImage(entityId);

        assertNotNull(actualEntityImage, "Картинка должна быть получена");
        assertArrayEquals(expectedItemImage.getData(), actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
        assertEquals(expectedItemImage.getMediaType(), actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
        assertEquals(expectedItemImage.getEntityId(), actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");

        entityImageService.updateEntityImage(entityId, new MockMultipartFile(
                "image",
                oldImagePath,
                oldEntityImage.getMediaType().toString(),
                oldEntityImage.getData()));
    }
}
