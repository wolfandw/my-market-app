package io.github.wolfandw.mymarket.cache.impl;

import io.github.wolfandw.mymarket.cache.ItemCache;
import io.github.wolfandw.mymarket.model.Item;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(ItemsCacheImpl.class);
    private static final String KEY_PREFIX = "mymarket:items:item";
    private static final String KEY_DELIMITER = ":";

    private final ReactiveRedisTemplate<String, Item> itemCacheTemplate;

    @Value("${mymarket.redis.time-to-live}")
    private Integer timeToLive;

    /**
     * Создает новый кэш товаров.
     *
     * @param itemCacheTemplate кэш товаров
     */
    public ItemCacheImpl(ReactiveRedisTemplate<String, Item> itemCacheTemplate) {
        this.itemCacheTemplate = itemCacheTemplate;
    }

    @Override
    public Mono<Item> getItem(Long itemId) {
        return itemCacheTemplate.opsForValue().get(buildKey(itemId));
    }

    @Override
    public Mono<Item> cache(Mono<Item> databaseItem) {
        return databaseItem
                .flatMap(item -> {
                            LOG.info("Помещаем в кэш товар");
                            return itemCacheTemplate.opsForValue()
                                    .set(buildKey(item.getId()), item, Duration.ofMinutes(timeToLive))
                                    .thenReturn(item);
                        }
                );
    }

    @Override
    public Mono<Long> clear(Long itemId) {
        return itemCacheTemplate.delete(buildKey(itemId));
    }

    private static @NonNull String buildKey(Long itemId) {
        return String.join(KEY_DELIMITER, KEY_PREFIX, itemId.toString());
    }
}
