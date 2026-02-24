package io.github.wolfandw.mymarket.cache.impl;

import io.github.wolfandw.mymarket.cache.ItemsCountCache;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Реализация {@link ItemsCountCache}
 */
@Component
public class ItemsCountCacheImpl implements ItemsCountCache {
    private static final String KEY_PREFIX = "mymarket:items:paging";

    private final ReactiveRedisTemplate<String, Long> itemsCountCacheTemplate;

    @Value("${mymarket.redis.time-to-live}")
    private Integer timeToLive;

    /**
     * Создает кэш количества товаров для пейджинга.
     *
     * @param itemsCountCacheTemplate кэш количества товаров для пейджинга
     */
    public ItemsCountCacheImpl(ReactiveRedisTemplate<String, Long> itemsCountCacheTemplate) {
        this.itemsCountCacheTemplate = itemsCountCacheTemplate;
    }

    @Override
    public Mono<Long> getItemsCount(String search, String sort, Integer pageNumber, Integer pageSize) {
        String key = buildKey(search, sort, pageNumber, pageSize);
        return itemsCountCacheTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Boolean> cache(String search, String sort, Integer pageNumber, Integer pageSize, Long count) {
        String key = buildKey(search, sort, pageNumber, pageSize);
        return itemsCountCacheTemplate.opsForValue().set(key, count, Duration.ofMinutes(timeToLive));
    }

    private static @NonNull String buildKey(String search, String sort, Integer pageNumber, Integer pageSize) {
        return String.join(":", KEY_PREFIX, search, sort, pageNumber.toString(), pageSize.toString());
    }
}
