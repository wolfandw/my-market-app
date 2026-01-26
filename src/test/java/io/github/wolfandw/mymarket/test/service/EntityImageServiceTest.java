package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Модульный тест сервиса картинок.
 */
public class EntityImageServiceTest extends AbstractTest {
    @Autowired
    private EntityImageService entityImageService;

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L, 0L, -1L})
    void getEntityImageTest(Long entityId) throws IOException {
        String mockPostImageName = entityId + ".jpg";
        Item mockItem = new Item();
        mockItem.setId(entityId);
        mockItem.setImgPath(mockPostImageName);
        Optional<Item> optionalMockItem = Optional.of(mockItem);

        EntityImageDto mockPostImage = new EntityImageDto(entityId, new byte[]{1,2,3}, MediaType.APPLICATION_OCTET_STREAM);
        when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
        when(fileStorageService.readFile(mockPostImageName)).thenReturn(mockPostImage.getData());

        EntityImageDto entityImage = entityImageService.getEntityImage(entityId);
        verify(fileStorageService).readFile(mockPostImageName);

        assertNotNull(entityImage, "Картинка поста должна быть получена");
        assertArrayEquals(mockPostImage.getData(), entityImage.getData(), "Данные картинки должны быть равны исходным");
        assertEquals(mockPostImage.getMediaType(), entityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
        assertEquals(mockPostImage.getEntityId(), entityImage.getEntityId(), "Идентификатор поста картинки должен быть равен исходному");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L, 0L, -1L})
    void updateEntityImageTest(Long entityId) throws IOException {
        String mockPostImageName = entityId + ".jpg";
        Item mockItem = new Item();
        mockItem.setId(entityId);
        mockItem.setImgPath(mockPostImageName);
        Optional<Item> optionalMockItem = Optional.of(mockItem);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "image",
                mockPostImageName,
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1,2,3});

        when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
        entityImageService.updateEntityImage(entityId, multipartFile);
        verify(fileStorageService).writeFile(mockPostImageName, multipartFile);
        verify(itemRepository).save(mockItem);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L, 0L, -1L})
    void deletePostImageTest(Long entityId) throws IOException {
            String mockPostImageName = entityId + ".jpg";
            Item mockItem = new Item();
            mockItem.setId(entityId);
            mockItem.setImgPath(mockPostImageName);
            Optional<Item> optionalMockItem = Optional.of(mockItem);

            when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
            entityImageService.deleteEntityImage(entityId);
            verify(fileStorageService).deleteFile(mockPostImageName);
            verify(itemRepository).save(mockItem);
    }
}
