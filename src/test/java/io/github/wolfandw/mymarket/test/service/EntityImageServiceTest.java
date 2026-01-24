package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Модульный тест сервиса картинок.
 */
public class EntityImageServiceTest extends AbstractTest {
    @Autowired
    private EntityImageService entityImageService;

    private Map<Long, EntityImageDto> images = new HashMap<>();

    @BeforeEach
    void setUp() {
        images = LongStream.range(1, 13).boxed().collect(HashMap::new,
                (m, entityId) ->
                        m.put(entityId, new EntityImageDto(entityId, new byte[0], MediaType.APPLICATION_OCTET_STREAM)),
                HashMap::putAll);
    }

    @Test
    void getEntityImageTest() {
        Long entityId = 5L;
        String mockPostImageName = entityId + ".jpg";
        EntityImageDto mockPostImage = images.get(entityId);
        Item mockItem = new Item();
        mockItem.setId(entityId);
        mockItem.setImgPath(mockPostImageName);
        Optional<Item> optionalMockItem = Optional.of(mockItem);
        when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
        EntityImageDto entityImage = entityImageService.getEntityImage(entityId);

        assertNotNull(entityImage, "Картинка поста должна быть получена");
        assertArrayEquals(mockPostImage.getData(), entityImage.getData(), "Данные картинки должны быть равны исходным");
        assertEquals(mockPostImage.getMediaType(), entityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
        assertEquals(mockPostImage.getEntityId(), entityImage.getEntityId(), "Идентификатор поста картинки должен быть равен исходному");
    }

    @Test
    void testUpdateEntityImageTest() {
        Long entityId = 5L;
        String mockPostImageName = entityId + ".jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(
                "image",
                mockPostImageName,
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]);

        Item mockItem = new Item();
        mockItem.setId(entityId);
        mockItem.setImgPath(mockPostImageName);
        Optional<Item> optionalMockItem = Optional.of(mockItem);

        when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
        entityImageService.updateEntityImage(entityId, multipartFile);
        verify(itemRepository).save(mockItem);
    }
}
