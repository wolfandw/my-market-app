package io.github.wolfandw.mymarket.test.cache;

import io.github.wolfandw.mymarket.cache.ItemsCache;
import io.github.wolfandw.mymarket.cache.impl.ItemsCacheImpl;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Модульный текст кэша списка товаров.
 */
public class ItemsCacheTest extends AbstractTest {
    private ReactiveRedisTemplate<String, Item> itemCacheTemplate;
    private ReactiveListOperations<String, Item> reactiveListOperations;
    private ItemsCache cache;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        itemCacheTemplate = mock(ReactiveRedisTemplate.class);
        reactiveListOperations = mock(ReactiveListOperations.class);

        when(itemCacheTemplate.opsForList()).thenReturn(reactiveListOperations);
        cache = new ItemsCacheImpl(itemCacheTemplate, 3);
    }

    @Test
    void getItemsTest() {
        List<Item> content = ITEMS.values().stream().limit(5).toList();
        Flux<Item> page = Flux.fromIterable(content);
        String key = String.join(":", ItemsCache.KEY_PREFIX, "", "NO", "1", "5");

        when(reactiveListOperations.range(key, 0, -1)).thenReturn(page);

        StepVerifier.create( cache.getItems("",  "NO", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    Assertions.assertThat(itemsPage).size().isEqualTo(5);
                    Assertions.assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 11");
                }).verifyComplete();
    }

    @Test
    void cacheTest() {
        List<Item> content = ITEMS.values().stream().limit(5).toList();
        Flux<Item> page = Flux.fromIterable(content);
        String key = String.join(":", ItemsCache.KEY_PREFIX, "", "NO", "1", "5");

        when(reactiveListOperations.rightPush(eq(key), any(Item.class))).thenReturn(Mono.just(1L));
        when(itemCacheTemplate.expire(eq(key), any(Duration.class))).thenReturn(Mono.just(true));

        StepVerifier.create( cache.cache("",  "NO", 1, 5, page).collectList()).
                assertNext(itemsPage -> {
                    Assertions.assertThat(itemsPage).size().isEqualTo(5);
                    Assertions.assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 11");
                }).verifyComplete();
    }

    @Test
    void clearTest() {
        String key = String.join(":", ItemsCache.KEY_PREFIX, "", "NO", "1", "5");

        when(itemCacheTemplate.keys(ItemsCache.KEY_PREFIX + ':' + '*')).thenReturn(Flux.just(key));
        when(itemCacheTemplate.delete(Flux.just(key))).thenReturn(Mono.just(1L));

        StepVerifier.create( cache.clear()).
                consumeNextWith(count -> {
                    Assertions.assertThat(count).isEqualTo(1L);
                }).expectComplete();
    }
}
