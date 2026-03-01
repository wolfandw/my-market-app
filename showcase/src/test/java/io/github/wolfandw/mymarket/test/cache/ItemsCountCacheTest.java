package io.github.wolfandw.mymarket.test.cache;

import io.github.wolfandw.mymarket.cache.ItemsCache;
import io.github.wolfandw.mymarket.cache.ItemsCountCache;
import io.github.wolfandw.mymarket.cache.impl.ItemsCountCacheImpl;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.Mockito.*;

/**
 * Модульный текст кэша количества товаров.
 */
public class ItemsCountCacheTest extends AbstractTest {
    private ReactiveRedisTemplate<String, Long> itemsCountCacheTemplate;
    private ReactiveValueOperations<String, Long> reactiveValueOperations;
    private ItemsCountCacheImpl cache;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        itemsCountCacheTemplate = mock(ReactiveRedisTemplate.class);
        reactiveValueOperations = mock(ReactiveValueOperations.class);

        when(itemsCountCacheTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        cache = new ItemsCountCacheImpl(itemsCountCacheTemplate, 3);
    }

    @Test
    void getItemsCountTest() {
        Long count = (long)ITEMS.size();
        Mono<Long> countMono = Mono.just(count);
        String key = String.join(":", ItemsCache.KEY_PREFIX, "", "NO", "1", "5");

        when(reactiveValueOperations.get(key)).thenReturn(countMono);

        StepVerifier.create( cache.getItemsCount("", 1, 5)).
                assertNext(cachedCount -> {
                    Assertions.assertThat(cachedCount).isEqualTo(5);
                }).expectComplete();
    }

    @Test
    void cacheTest() {
        Long count = (long)ITEMS.size();
        Mono<Long> countMono = Mono.just(count);
        String key = String.join(":", ItemsCountCache.KEY_PREFIX, "", "1", "5");

        when(reactiveValueOperations.set(eq(key), any(Long.class), any(Duration.class))).thenReturn(Mono.just(true));

        StepVerifier.create( cache.cache("", 1, 5, countMono)).
                consumeNextWith(cachedCount -> {
                    Assertions.assertThat(cachedCount).isEqualTo(13);
                }).verifyComplete();
    }

    @Test
    void clearTest() {
        String key = String.join(":", ItemsCountCache.KEY_PREFIX, "", "1", "5");

        when(itemsCountCacheTemplate.keys(ItemsCountCache.KEY_PREFIX + ':' + '*')).thenReturn(Flux.just(key));
        when(itemsCountCacheTemplate.delete(Flux.just(key))).thenReturn(Mono.just(1L));

        StepVerifier.create( cache.clear()).
                consumeNextWith(count -> {
                    Assertions.assertThat(count).isEqualTo(1L);
                }).expectComplete();
    }
}
