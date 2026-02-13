package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса картинок.
 */
public class EntityImageServiceTest extends AbstractServiceTest {
    private static final String PARAMETER_IMAGE_FILE = "imageFile";

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    void getEntityImageTest(Long entityId) throws IOException {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockImageName = mockItem.getImgPath();
        Mono<Item> mockItemMono = Mono.just(mockItem);

        when(itemRepository.findById(entityId)).thenReturn(mockItemMono);
        when(fileStorageService.readFile(mockImageName)).thenReturn(Mono.just(new byte[]{1, 2, 3}));

        StepVerifier.create(fileStorageService.readFile(mockImageName).zipWith(entityImageService.getEntityImage(entityId))).consumeNextWith(tuple -> {
            byte[] expectedImageData = tuple.getT1();
            EntityImageDto actualEntityImage = tuple.getT2();
            assertNotNull(actualEntityImage, "Картинка должна быть получена");
            assertArrayEquals(expectedImageData, actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
            assertEquals(MediaType.APPLICATION_OCTET_STREAM, actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
            assertEquals(entityId, actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");
        }).verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    void getEntityImageBase64Test(Long entityId) throws IOException {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockImageName = mockItem.getImgPath();
        Mono<Item> mockItemMono = Mono.just(mockItem);

        when(itemRepository.findById(entityId)).thenReturn(mockItemMono);
        when(fileStorageService.readFile(mockImageName)).thenReturn(Mono.just(new byte[]{1, 2, 3}));

        StepVerifier.create(fileStorageService.readFile(mockImageName).zipWith(entityImageService.getEntityImageBase64(entityId))).consumeNextWith(tuple -> {
            byte[] expectedImageData = tuple.getT1();
            String base64Encoded = new String(Base64.getEncoder().encode(expectedImageData), StandardCharsets.UTF_8);
            String expectedItemImageBase64 = "data:" + MediaType.APPLICATION_OCTET_STREAM + ";base64, " + base64Encoded;

            String actualEntityImageBase64 = tuple.getT2();
            assertNotNull(actualEntityImageBase64, "Картинка должна быть получена");
            assertEquals(expectedItemImageBase64, actualEntityImageBase64, "Данные картинки должны быть равны исходным");
        }).verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    void setEntityImageTest(Long entityId) throws IOException {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockImageName = mockItem.getImgPath();
        mockItem();
        FilePart expectedFilePart = getFilePart(mockImageName);
        Mono<FilePart> expectedFilePartMono = Mono.just(expectedFilePart);
        when(fileStorageService.writeFile(mockImageName, expectedFilePart)).thenReturn(Mono.just(mockImageName));
        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(mockItem));
        trxStepVerifier.create(entityImageService.setEntityImage(entityId, expectedFilePartMono)).verifyComplete();
    }
}
