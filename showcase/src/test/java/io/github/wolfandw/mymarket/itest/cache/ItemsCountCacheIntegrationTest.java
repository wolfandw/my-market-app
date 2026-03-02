package io.github.wolfandw.mymarket.itest.cache;

import io.github.wolfandw.mymarket.cache.ItemsCountCache;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Интеграционный тест кэша количества товаров.
 */
public class ItemsCountCacheIntegrationTest  extends AbstractIntegrationTest {
    private static final String KEY = String.join(":", ItemsCountCache.KEY_PREFIX, "", "1", "5");
    private static final Duration DURATION = Duration.ofSeconds(2);

    @Test
    void getItemsEmptyTest() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Проверим получение пустого кэша через компонент
        trxStepVerifier.create(itemsCountCache.getItemsCount("", 1, 5)).verifyComplete();
    }

    @Test
    void getItemsNoEmptyTest() throws InterruptedException {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Проверим получение непустого кэша через компонент
        trxStepVerifier.create(itemsCountCache.getItemsCount("", 1, 5)).
                assertNext(cachedCount -> Assertions.assertThat(cachedCount).isEqualTo(13)).verifyComplete();

        // Убедимся что после окончания времени жизни кэш очистился
        TimeUnit.SECONDS.sleep(2L);
        checkEmptyCache();
    }

    @Test
    void cacheTest() throws InterruptedException {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные через компонент
        trxStepVerifier.create(itemsCountCache.cache("", 1, 5, itemRepository.count())).
                assertNext(cachedCount -> Assertions.assertThat(cachedCount).isEqualTo(13)).verifyComplete();

        // Убедимся что данные есть в кэше
        checkNoEmptyCache();

        // Убедимся что после окончания времени жизни кэш очистился
        TimeUnit.SECONDS.sleep(2L);
        checkEmptyCache();
    }

    @Test
    void clearTest() {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Очистим кэш через компонент
        trxStepVerifier.create(itemsCountCache.clear()).
                consumeNextWith(deletedCount -> Assertions.assertThat(deletedCount).isEqualTo(1L)).verifyComplete();

        // Убедимся что кэш очистился сразу
        checkEmptyCache();
    }

    private void fillAndCheckNoEmptyCache() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные
        trxStepVerifier.create(itemRepository.count()
                        .flatMap(count -> itemsCountCacheTemplate.opsForValue()
                                .set(KEY, count, DURATION)
                                .thenReturn(count)
                        )).
                assertNext(cachedCount -> Assertions.assertThat(cachedCount).isEqualTo(13)).verifyComplete();

        // Убедимся, что данные есть в кэше
        checkNoEmptyCache();
    }

    private void checkEmptyCache() {
        trxStepVerifier.create(itemsCountCacheTemplate.opsForValue().get(KEY)).verifyComplete();
    }

    private void checkNoEmptyCache() {
        trxStepVerifier.create(itemsCountCacheTemplate.opsForValue().get(KEY)).
                assertNext(cachedCount -> Assertions.assertThat(cachedCount).isEqualTo(13)).verifyComplete();
    }
}
