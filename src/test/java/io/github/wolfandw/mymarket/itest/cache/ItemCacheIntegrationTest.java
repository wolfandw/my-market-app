package io.github.wolfandw.mymarket.itest.cache;

import io.github.wolfandw.mymarket.cache.ItemCache;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class ItemCacheIntegrationTest extends AbstractIntegrationTest {
    private static final Long ITEM_ID = 1L;
    private static final String KEY = String.join(":", ItemCache.KEY_PREFIX, ITEM_ID.toString());
    private static final Duration DURATION = Duration.ofSeconds(2);

    @Test
    void getItemsEmptyTest() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Проверим получение пустого кэша через компонент
        trxStepVerifier.create(itemCache.getItem(ITEM_ID)).verifyComplete();
    }

    @Test
    void getItemsNoEmptyTest() throws InterruptedException {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Проверим получение непустого кэша через компонент
        trxStepVerifier.create(itemCache.getItem(ITEM_ID)).
                assertNext(cachedItem -> Assertions.assertThat(cachedItem.getTitle()).isEqualTo("Item 07 SearchTag")).verifyComplete();

        // Убедимся что после окончания времени жизни кэш очистился
        TimeUnit.SECONDS.sleep(2L);
        checkEmptyCache();
    }

    @Test
    void cacheTest() throws InterruptedException {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные через компонент
        trxStepVerifier.create(itemCache.cache(itemRepository.findById(ITEM_ID))).
                assertNext(cachedItem -> Assertions.assertThat(cachedItem.getTitle()).isEqualTo("Item 07 SearchTag")).verifyComplete();

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
        trxStepVerifier.create(itemCache.delete(ITEM_ID)).
                consumeNextWith(deletedCount -> Assertions.assertThat(deletedCount).isEqualTo(1L)).verifyComplete();

        // Убедимся что кэш очистился сразу
        checkEmptyCache();
    }

    @Test
    void clearTest() {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Очистим кэш через компонент
        trxStepVerifier.create(itemCache.clear()).
                consumeNextWith(deletedCount -> Assertions.assertThat(deletedCount).isEqualTo(1L)).verifyComplete();

        // Убедимся что кэш очистился сразу
        checkEmptyCache();
    }

    private void fillAndCheckNoEmptyCache() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные
        trxStepVerifier.create(  itemRepository.findById(ITEM_ID)
                        .flatMap(item -> itemCacheTemplate.opsForValue()
                                .set(KEY, item, DURATION)
                                .thenReturn(item)
                        )).
                assertNext(cachedItem -> Assertions.assertThat(cachedItem.getTitle()).isEqualTo("Item 07 SearchTag")).verifyComplete();

        // Убедимся, что данные есть в кэше
        checkNoEmptyCache();
    }

    private void checkEmptyCache() {
        StepVerifier.create(itemCacheTemplate.opsForValue().get(KEY)).verifyComplete();
    }

    private void checkNoEmptyCache() {
        trxStepVerifier.create(itemCacheTemplate.opsForValue().get(KEY)).
                assertNext(cachedItem -> Assertions.assertThat(cachedItem.getTitle()).isEqualTo("Item 07 SearchTag")).verifyComplete();
    }
}
