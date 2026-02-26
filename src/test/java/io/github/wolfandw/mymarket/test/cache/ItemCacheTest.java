package io.github.wolfandw.mymarket.test.cache;

import io.github.wolfandw.mymarket.cache.ItemCache;
import io.github.wolfandw.mymarket.cache.impl.ItemCacheImpl;
import io.github.wolfandw.mymarket.model.Item;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Модульный текст кэша товаров.
 */
public class ItemCacheTest extends AbstractTest {
    private ReactiveRedisTemplate<String, Item> itemCacheTemplate;
    private ReactiveValueOperations<String, Item> reactiveValueOperations;
    private ItemCache cache;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        itemCacheTemplate = mock(ReactiveRedisTemplate.class);
        reactiveValueOperations = mock(ReactiveValueOperations.class);

        when(itemCacheTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        cache = new ItemCacheImpl(itemCacheTemplate, 3);
    }

    @Test
    void getItemTest() {
        Long itemId = 1L;
        Mono<Item> item = Mono.just( ITEMS.get(itemId));

        String key = String.join(":", ItemCache.KEY_PREFIX, itemId.toString());

        when(reactiveValueOperations.get(key)).thenReturn(item);

        StepVerifier.create( cache.getItem(itemId)).
                consumeNextWith(cachedItem -> {
                    assertThat(cachedItem.getTitle()).isEqualTo("Item 07 SearchTag");
                }).verifyComplete();
    }

    @Test
    void cacheTest() {
        Long itemId = 1L;
        Mono<Item> item = Mono.just( ITEMS.get(itemId));

        String key = String.join(":", ItemCache.KEY_PREFIX, itemId.toString());

        when(reactiveValueOperations.set(eq(key), any(Item.class), any(Duration.class))).thenReturn(Mono.just(true));

        StepVerifier.create( cache.cache(item)).
                consumeNextWith(cachedItem -> {
                    assertThat(cachedItem.getTitle()).isEqualTo("Item 07 SearchTag");
                }).verifyComplete();
    }

    @Test
    void deleteTest() {
        Long itemId = 1L;
        String key = String.join(":", ItemCache.KEY_PREFIX, itemId.toString());;

        when(itemCacheTemplate.delete(Flux.just(key))).thenReturn(Mono.just(1L));

        StepVerifier.create(cache.delete(itemId)).
                consumeNextWith(count -> {
                    Assertions.assertThat(count).isEqualTo(1L);
                }).expectComplete();
    }

    @Test
    void clearTest() {
        Long itemId = 1L;
        String key = String.join(":", ItemCache.KEY_PREFIX, itemId.toString());;

        when(itemCacheTemplate.delete(Flux.just(key))).thenReturn(Mono.just(1L));

        StepVerifier.create(cache.clear()).
                consumeNextWith(count -> {
                    Assertions.assertThat(count).isEqualTo(1L);
                }).expectComplete();
    }
}
