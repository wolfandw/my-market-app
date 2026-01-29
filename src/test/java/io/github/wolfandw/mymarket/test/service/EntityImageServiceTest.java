package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.dto.DtoConstants;
import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса картинок.
 */
public class EntityImageServiceTest extends AbstractServiceTest {
    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    void getEntityImageTest(Long entityId) throws IOException {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockPostImageName = mockItem.getImgPath();
        Optional<Item> optionalMockItem = Optional.of(mockItem);

        EntityImageDto mockPostImage = new EntityImageDto(entityId, new byte[]{1, 2, 3}, MediaType.APPLICATION_OCTET_STREAM);
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
    @ValueSource(longs = {1L, 13L})
    void getEntityImageBase64Test(Long entityId) throws IOException {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockPostImageName = mockItem.getImgPath();
        Optional<Item> optionalMockItem = Optional.of(mockItem);

        byte[] expectedImageData = new byte[]{1, 2, 3};
        String base64Encoded = new String(Base64.getEncoder().encode(expectedImageData), StandardCharsets.UTF_8);
        String expectedItemImageBase64 = "data:" + MediaType.APPLICATION_OCTET_STREAM + ";base64, " + base64Encoded;

        when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
        when(fileStorageService.readFile(mockPostImageName)).thenReturn(expectedImageData);

        String actualEntityImageBase64 = entityImageService.getEntityImageBase64(entityId);
        verify(fileStorageService).readFile(mockPostImageName);

        assertNotNull(actualEntityImageBase64, "Картинка поста должна быть получена");
        assertEquals(expectedItemImageBase64, actualEntityImageBase64, "Данные картинки должны быть равны исходным");
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    void updateEntityImageTest(Long entityId) throws IOException {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockPostImageName = mockItem.getImgPath();
        Optional<Item> optionalMockItem = Optional.of(mockItem);

        MockMultipartFile multipartFile = new MockMultipartFile(
                DtoConstants.PARAMETER_IMAGE_FILE,
                mockPostImageName,
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{1, 2, 3});

        when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
        entityImageService.updateEntityImage(entityId, multipartFile);
        verify(fileStorageService).writeFile(mockPostImageName, multipartFile);
        verify(itemRepository).save(mockItem);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    void deletePostImageTest(Long entityId) throws IOException {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockPostImageName = mockItem.getImgPath();
        Optional<Item> optionalMockItem = Optional.of(mockItem);

        when(itemRepository.findById(entityId)).thenReturn(optionalMockItem);
        entityImageService.deleteEntityImage(entityId);
        verify(fileStorageService).deleteFile(mockPostImageName);
        verify(itemRepository).save(mockItem);
    }
}
