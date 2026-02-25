package io.github.wolfandw.mymarket.test.cache;

import io.github.wolfandw.mymarket.cache.EntityImageCache;
import io.github.wolfandw.mymarket.cache.impl.EntityImageCacheImpl;
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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Модульный тест кэша картинок.
 */
public class EntityImageCacheTest extends AbstractTest {
    private ReactiveRedisTemplate<String, byte[]> entityImageCacheTemplate;
    private ReactiveValueOperations<String, byte[]> reactiveValueOperations;
    private EntityImageCache cache;

    @BeforeEach
    @Override
    protected void setUp() {
        super.setUp();
        entityImageCacheTemplate = mock(ReactiveRedisTemplate.class);
        reactiveValueOperations = mock(ReactiveValueOperations.class);

        when(entityImageCacheTemplate.opsForValue()).thenReturn(reactiveValueOperations);
        cache = new EntityImageCacheImpl(entityImageCacheTemplate, 3);
    }

    @Test
    void getEntityImage() {
        Long entityId = 1L;
        byte[] content = {1, 2, 3};
        Mono<byte[]> contentMono = Mono.just(content);

        String key = String.join(":", EntityImageCache.KEY_PREFIX, entityId.toString());

        when(reactiveValueOperations.get(key)).thenReturn(contentMono);

        StepVerifier.create( cache.getEntityImage(entityId)).
                consumeNextWith(cachedContent -> {
                    assertArrayEquals(cachedContent, content);
                }).verifyComplete();
    }

    @Test
    void cache() {
        Long entityId = 1L;
        byte[] content = {1, 2, 3};
        Mono<byte[]> contentMono = Mono.just(content);

        String key = String.join(":", EntityImageCache.KEY_PREFIX, entityId.toString());

        when(reactiveValueOperations.set(eq(key), any(byte[].class), any(Duration.class))).thenReturn(Mono.just(true));

        StepVerifier.create( cache.cache(entityId, contentMono)).
                consumeNextWith(cachedContent -> {
                    assertArrayEquals(cachedContent, content);
                }).verifyComplete();
    }

    @Test
    void clear() {
        Long entityId = 1L;
        String key = String.join(":", EntityImageCache.KEY_PREFIX, entityId.toString());

        when(entityImageCacheTemplate.delete(Flux.just(key))).thenReturn(Mono.just(1L));

        StepVerifier.create(cache.clear(entityId)).
                consumeNextWith(count -> {
                    Assertions.assertThat(count).isEqualTo(1L);
                }).expectComplete();
    }
}
