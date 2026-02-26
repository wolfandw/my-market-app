package io.github.wolfandw.mymarket.itest.cache;

import io.github.wolfandw.mymarket.cache.EntityImageCache;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.mymarket.model.Item;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Интеграционный тест картинок.
 */
public class EntityImageIntegrationCacheTest extends AbstractIntegrationTest {
    private static final Long ENTITY_ID = 1L;
    private static final String KEY = String.join(":", EntityImageCache.KEY_PREFIX, ENTITY_ID.toString());
    private static final Duration DURATION = Duration.ofSeconds(2);

    @BeforeEach
    void setup() {
        setupImages();
    }

    @AfterEach
    void cleanUp() {
        cleanUpImages();
    }

    @Test
    void getEntityImageEmptyTest() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Проверим получение пустого кэша через компонент
        trxStepVerifier.create(entityImageCache.getEntityImage(ENTITY_ID)).verifyComplete();
    }

    @Test
    void getEntityImageNoEmptyTest() throws InterruptedException {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Проверим получение непустого кэша через компонент
        trxStepVerifier.create(entityImageCache.getEntityImage(ENTITY_ID)).
                assertNext(cachedContent ->  Assertions.assertThat(cachedContent).isNotEmpty()).verifyComplete();

        // Убедимся что после окончания времени жизни кэш очистился
        TimeUnit.SECONDS.sleep(2L);
        checkEmptyCache();
    }

    @Test
    void cacheTest() throws InterruptedException {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные через компонент
        trxStepVerifier.create(entityImageCache.cache(ENTITY_ID, itemRepository.findById(ENTITY_ID).map(Item::getImgPath).flatMap(fileStorageService::readFile))).
                assertNext(cachedContent ->  Assertions.assertThat(cachedContent).isNotEmpty()).verifyComplete();

        // Убедимся что данные есть в кэше
        checkNoEmptyCache();

        // Убедимся что после окончания времени жизни кэш очистился
        TimeUnit.SECONDS.sleep(2L);
        checkEmptyCache();
    }

    @Test
    void deleteTest() {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Очистим кэш через компонент
        trxStepVerifier.create(entityImageCache.delete(ENTITY_ID)).
                consumeNextWith(deletedCount -> Assertions.assertThat(deletedCount).isEqualTo(1L)).verifyComplete();

        // Убедимся что кэш очистился сразу
        checkEmptyCache();
    }

    @Test
    void clearTest() {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Очистим кэш через компонент
        trxStepVerifier.create(entityImageCache.clear()).
                consumeNextWith(deletedCount -> Assertions.assertThat(deletedCount).isEqualTo(1L)).verifyComplete();

        // Убедимся что кэш очистился сразу
        checkEmptyCache();
    }

    private void fillAndCheckNoEmptyCache() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные
        trxStepVerifier.create(itemRepository.findById(ENTITY_ID).map(Item::getImgPath).flatMap(fileStorageService::readFile)
                        .flatMap(cachedContent -> entityImageCacheTemplate.opsForValue()
                                .set(KEY, cachedContent, DURATION)
                                .thenReturn(cachedContent)
                        )).
                assertNext(cachedContent ->  Assertions.assertThat(cachedContent).isNotEmpty()).verifyComplete();

        // Убедимся, что данные есть в кэше
        checkNoEmptyCache();
    }

    private void checkEmptyCache() {
        trxStepVerifier.create(entityImageCacheTemplate.opsForValue().get(KEY)).verifyComplete();
    }

    private void checkNoEmptyCache() {
        trxStepVerifier.create(entityImageCacheTemplate.opsForValue().get(KEY)).
                assertNext(cachedContent ->  Assertions.assertThat(cachedContent).isNotEmpty()).verifyComplete();
    }
}
