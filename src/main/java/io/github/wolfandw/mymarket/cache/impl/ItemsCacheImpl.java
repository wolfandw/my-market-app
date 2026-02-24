package io.github.wolfandw.mymarket.cache.impl;

import io.github.wolfandw.mymarket.cache.ItemsCache;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.service.impl.FileStorageServiceImpl;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Реализация {@link ItemsCache}
 */
@Component
public class ItemsCacheImpl implements ItemsCache {
    private static final Logger LOG = LoggerFactory.getLogger(ItemsCacheImpl.class);
    private static final String KEY_PREFIX = "mymarket:items:list";
    private static final String KEY_DELIMITER = ":";

    private final ReactiveRedisTemplate<String, Item> itemCacheTemplate;

    @Value("${mymarket.redis.time-to-live}")
    private Integer timeToLive;

    /**
     * Создает кэш списка товаров.
     *
     * @param itemCacheTemplate кэш товаров
     */
    public ItemsCacheImpl(ReactiveRedisTemplate<String, Item> itemCacheTemplate) {
        this.itemCacheTemplate = itemCacheTemplate;
    }

    @Override
    public Flux<Item> getItems(String search, String sort, Integer pageNumber, Integer pageSize) {
        String key = buildKey(search, sort, pageNumber, pageSize);
        return itemCacheTemplate.opsForList().range(key, 0, -1);
    }

    @Override
    public Flux<Item> cache(String search, String sort, Integer pageNumber, Integer pageSize, Flux<Item> databaseItems) {
        String key = buildKey(search, sort, pageNumber, pageSize);
        return databaseItems
                .flatMap(item -> {
                            LOG.info("Помещаем в кэш список товаров");
                            return itemCacheTemplate.opsForList().rightPush(key, item)
                                    .then(itemCacheTemplate.expire(key, Duration.ofMinutes(timeToLive)))
                                    .thenReturn(item);
                        }
                );
    }

    @Override
    public Mono<Long> clear() {
        return itemCacheTemplate.delete(itemCacheTemplate.keys(KEY_PREFIX + ':' + '*'));
    }

    private static @NonNull String buildKey(String search, String sort, Integer pageNumber, Integer pageSize) {
        return String.join(KEY_DELIMITER, KEY_PREFIX, search, sort, pageNumber.toString(), pageSize.toString());
    }
}
