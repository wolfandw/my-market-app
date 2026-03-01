package io.github.wolfandw.mymarket.itest.cache;

import io.github.wolfandw.mymarket.cache.ItemsCache;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Интеграционный тест кэша списка товаров.
 */
public class ItemsCacheIntegrationTest extends AbstractIntegrationTest {
    private static final String KEY = String.join(":", ItemsCache.KEY_PREFIX, "", "NO", "1", "5");
    private static final Pageable PAGEABLE = PageRequest.of(0, 5, Sort.unsorted());
    private static final  Duration DURATION = Duration.ofSeconds(2);

    @Test
    void getItemsEmptyTest() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Проверим получение пустого кэша через компонент
        trxStepVerifier.create(itemsCache.getItems("", "NO", 1, 5).collectList()).
                assertNext(cachedItems -> Assertions.assertThat(cachedItems).size().isEqualTo(0)).verifyComplete();
    }

    @Test
    void getItemsNoEmptyTest() throws InterruptedException {
        // Заполним кэш
        fillAndCheckNoEmptyCache();

        // Проверим получение непустого кэша через компонент
        trxStepVerifier.create(itemsCache.getItems("", "NO", 1, 5).collectList()).
                assertNext(cachedItems -> Assertions.assertThat(cachedItems).size().isEqualTo(5)).verifyComplete();

        // Убедимся что после окончания времени жизни кэш очистился
        TimeUnit.SECONDS.sleep(2L);
        checkEmptyCache();
    }

    @Test
    void cacheTest() throws InterruptedException {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные через компонент
        trxStepVerifier.create(itemsCache.cache("", "NO", 1, 5, itemRepository.findAllBy(PAGEABLE)).collectList()).
                assertNext(cachedItems -> Assertions.assertThat(cachedItems).size().isEqualTo(5)).verifyComplete();

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
        trxStepVerifier.create(itemsCache.clear()).
                consumeNextWith(deletedCount -> Assertions.assertThat(deletedCount).isEqualTo(1L)).verifyComplete();

        // Убедимся что кэш очистился сразу
        checkEmptyCache();
    }

    private void fillAndCheckNoEmptyCache() {
        // Убедимся, что кэш изначально пустой
        checkEmptyCache();

        // Поместим в кэш данные
        trxStepVerifier.create(itemRepository.findAllBy(PAGEABLE).flatMap(item ->
                        itemCacheTemplate.opsForList().rightPush(KEY, item)
                                .then(itemCacheTemplate.expire(KEY, DURATION))
                                .thenReturn(item)).collectList()).
                assertNext(cachedItems -> Assertions.assertThat(cachedItems).size().isEqualTo(5)).verifyComplete();

        // Убедимся, что данные есть в кэше
        checkNoEmptyCache();
    }

    private void checkCacheSize(int size) {
        trxStepVerifier.create(itemCacheTemplate.opsForList().range(KEY, 0, -1).collectList()).
                assertNext(cachedItems -> Assertions.assertThat(cachedItems).size().isEqualTo(size)).verifyComplete();
    }

    private void checkEmptyCache() {
        checkCacheSize(0);
    }

    private void checkNoEmptyCache() {
        checkCacheSize(5);
    }
}
