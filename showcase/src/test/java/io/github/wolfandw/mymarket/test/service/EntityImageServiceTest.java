package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.model.User;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.authorization.AuthorizationDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Модульный тест сервиса картинок.
 */
public class EntityImageServiceTest extends AbstractServiceTest {
    @ParameterizedTest
    @MethodSource("provideArgs")
    @IsRoleUser
    public void getEntityImageUserTest(Long entityId, boolean emptyCache) {
        getEntityImageTest(entityId, emptyCache, getUserMono());
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    @IsRoleAdmin
    public void getEntityImageAdminTest(Long entityId, boolean emptyCache) {
        getEntityImageTest(entityId, emptyCache, getAdminMono());
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    @IsRoleGuest
    public void getEntityImageGuestTest(Long entityId, boolean emptyCache) {
        getEntityImageTest(entityId, emptyCache, getGuestMono());
    }

    private void getEntityImageTest(Long entityId, boolean emptyCache, Mono<User> testUserMono) {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockImageName = mockItem.getImgPath();
        Mono<Item> mockItemMono = Mono.just(mockItem);
        Mono<byte[]> contentMono = Mono.just(new byte[]{1, 2, 3});

        when(itemRepository.findById(entityId)).thenReturn(mockItemMono);
        when(fileStorageService.readFile(mockImageName)).thenReturn(contentMono);

        when(itemCache.getItem(entityId)).thenReturn(emptyCache ? Mono.empty() : mockItemMono);
        when(itemCache.cache(mockItemMono)).thenReturn(mockItemMono);

        when(entityImageCache.getEntityImage(entityId)).thenReturn(emptyCache ? Mono.empty() : contentMono);
        when(entityImageCache.cache(eq(entityId), any())).thenReturn(contentMono);

        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);

        StepVerifier.create(fileStorageService.readFile(mockImageName).zipWith(entityImageService.getEntityImage(entityId))).consumeNextWith(tuple -> {
            byte[] expectedImageData = tuple.getT1();
            EntityImageDto actualEntityImage = tuple.getT2();
            assertNotNull(actualEntityImage, "Картинка должна быть получена");
            assertArrayEquals(expectedImageData, actualEntityImage.getData(), "Данные картинки должны быть равны исходным");
            assertEquals(MediaType.APPLICATION_OCTET_STREAM, actualEntityImage.getMediaType(), "Тип данных картинки должен быть равен исходному");
            assertEquals(entityId, actualEntityImage.getEntityId(), "Идентификатор сущности картинки должен быть равен исходному");
        }).verifyComplete();

        verify(entityImageCache, times(emptyCache ? 1 : 0)).cache(eq(entityId), any());
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    @IsRoleUser
    public void getEntityImageBase64UserTest(Long entityId, boolean emptyCache) {
        getEntityImageBase64Test(entityId, emptyCache, getUserMono());
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    @IsRoleAdmin
    public void getEntityImageBase64AdminTest(Long entityId, boolean emptyCache) {
        getEntityImageBase64Test(entityId, emptyCache, getAdminMono());
    }

    @ParameterizedTest
    @MethodSource("provideArgs")
    @IsRoleGuest
    public void getEntityImageBase64GuestTest(Long entityId, boolean emptyCache) {
        getEntityImageBase64Test(entityId, emptyCache, getGuestMono());
    }

    private void getEntityImageBase64Test(Long entityId, boolean emptyCache, Mono<User> testUserMono) {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockImageName = mockItem.getImgPath();
        Mono<Item> mockItemMono = Mono.just(mockItem);
        Mono<byte[]> contentMono = Mono.just(new byte[]{1, 2, 3});

        when(itemRepository.findById(entityId)).thenReturn(mockItemMono);
        when(fileStorageService.readFile(mockImageName)).thenReturn(contentMono);

        when(itemCache.getItem(entityId)).thenReturn(emptyCache ? Mono.empty() : mockItemMono);
        when(itemCache.cache(mockItemMono)).thenReturn(mockItemMono);

        when(entityImageCache.getEntityImage(entityId)).thenReturn(emptyCache ? Mono.empty() : contentMono);
        when(entityImageCache.cache(eq(entityId), any())).thenReturn(contentMono);

        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);

        StepVerifier.create(fileStorageService.readFile(mockImageName).zipWith(entityImageService.getEntityImageBase64(entityId))).consumeNextWith(tuple -> {
            byte[] expectedImageData = tuple.getT1();
            String base64Encoded = new String(Base64.getEncoder().encode(expectedImageData), StandardCharsets.UTF_8);
            String expectedItemImageBase64 = "data:" + MediaType.APPLICATION_OCTET_STREAM + ";base64, " + base64Encoded;

            String actualEntityImageBase64 = tuple.getT2();
            assertNotNull(actualEntityImageBase64, "Картинка должна быть получена");
            assertEquals(expectedItemImageBase64, actualEntityImageBase64, "Данные картинки должны быть равны исходным");
        }).verifyComplete();

        verify(entityImageCache, times(emptyCache ? 1 : 0)).cache(eq(entityId), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    @IsRoleUser
    public void setEntityImageUserTest(Long entityId) {
        trxStepVerifier.create(entityImageService.setEntityImage(entityId, getFilePartMono(entityId, getUserMono()))).
                verifyError(AuthorizationDeniedException.class);
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    @IsRoleAdmin
    public void setEntityImageAdminTest(Long entityId) {
        trxStepVerifier.create(entityImageService.setEntityImage(entityId, getFilePartMono(entityId, getAdminMono()))).
                verifyComplete();
        verify(entityImageCache, never()).cache(eq(entityId), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L, 13L})
    @IsRoleGuest
    public void setEntityImageGuestTest(Long entityId) {
        trxStepVerifier.create(entityImageService.setEntityImage(entityId, getFilePartMono(entityId, getGuestMono()))).
                verifyError(AuthorizationDeniedException.class);
    }

    private Mono<FilePart> getFilePartMono(Long entityId, Mono<User> testUserMono) {
        Item mockItem = AbstractServiceTest.ITEMS.get(entityId);
        String mockImageName = mockItem.getImgPath();

        mockItem();
        mockGetItemFromCache();
        mockCacheItemFromCache();

        FilePart expectedFilePart = getFilePart(mockImageName);
        when(fileStorageService.writeFile(mockImageName, expectedFilePart)).thenReturn(Mono.just(mockImageName));
        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(mockItem));
        when(entityImageCache.delete(entityId)).thenReturn(Mono.just(1L));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);
        return Mono.just(expectedFilePart);
    }

    private static Stream<Arguments> provideArgs() {
        return Stream.of(
                Arguments.of(1L, true),
                Arguments.of(13L, true),
                Arguments.of(1L, false),
                Arguments.of(13L, false)
        );
    }
}
