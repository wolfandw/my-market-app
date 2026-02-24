package io.github.wolfandw.mymarket.cache.impl;

import io.github.wolfandw.mymarket.cache.ItemCache;
import io.github.wolfandw.mymarket.model.Item;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Реализация {@link ItemCache}
 */
@Component
public class ItemCacheImpl implements ItemCache {
    private static final String KEY_PREFIX = "mymarket:items:item";

    private final ReactiveRedisTemplate<String, Item> itemCacheTemplate;

    @Value("${mymarket.redis.time-to-live}")
    private Integer timeToLive;

    /**
     * Создает новый кэш товаров.
     *
     * @param itemCacheTemplate шаблона кэша товаров
     */
    public ItemCacheImpl(ReactiveRedisTemplate<String, Item> itemCacheTemplate) {
        this.itemCacheTemplate = itemCacheTemplate;
    }

    @Override
    public Mono<Item> getItem(Long id) {
        String key = buildKey(id);
        return itemCacheTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Boolean> cache(Long id, Item item) {
        String key = buildKey(id);
        return itemCacheTemplate.opsForValue().set(key, item, Duration.ofMinutes(timeToLive));
    }

    private static @NonNull String buildKey(Long id) {
        return String.join(":" , KEY_PREFIX, id.toString());
    }
}
