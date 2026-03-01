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
    private static final String KEY_DELIMITER = ":";

    private final ReactiveRedisTemplate<String, Item> itemCacheTemplate;
    private final Duration timeToLive;

    /**
     * Создает новый кэш товаров.
     *
     * @param itemCacheTemplate кэш товаров
     * @param timeToLive время жизни
     */
    public ItemCacheImpl(ReactiveRedisTemplate<String, Item> itemCacheTemplate,
                         @Value("${mymarket.redis.time-to-live}") Integer timeToLive) {
        this.itemCacheTemplate = itemCacheTemplate;
        this.timeToLive = Duration.ofSeconds(timeToLive);
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
                                    .set(buildKey(item.getId()), item, timeToLive)
                                    .thenReturn(item);
                        }
                );
    }

    @Override
    public Mono<Long> delete(Long itemId) {
        return itemCacheTemplate.delete(buildKey(itemId));
    }

    @Override
    public Mono<Long> clear() {
        return itemCacheTemplate.delete(itemCacheTemplate.keys(KEY_PREFIX + ':' + '*'));
    }

    private static @NonNull String buildKey(Long itemId) {
        return String.join(KEY_DELIMITER, KEY_PREFIX, itemId.toString());
    }
}
