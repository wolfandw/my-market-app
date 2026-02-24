package io.github.wolfandw.mymarket.cache.impl;

import io.github.wolfandw.mymarket.cache.ItemsCountCache;
import io.github.wolfandw.mymarket.service.impl.FileStorageServiceImpl;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(ItemsCountCacheImpl.class);
    private static final String KEY_PREFIX = "mymarket:items:paging";
    private static final String KEY_DELIMITER = ":";

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
    public Mono<Long> getItemsCount(String search, Integer pageNumber, Integer pageSize) {
        String key = buildKey(search, pageNumber, pageSize);
        return itemsCountCacheTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<Long> cache(String search, Integer pageNumber, Integer pageSize, Mono<Long> databaseCount) {
        String key = buildKey(search, pageNumber, pageSize);
        return databaseCount
                .flatMap(count -> {
                            LOG.info("Помещаем в кэш количество товаров");
                            return itemsCountCacheTemplate.opsForValue()
                                    .set(key, count, Duration.ofMinutes(timeToLive))
                                    .thenReturn(count);
                        }
                );
    }

    @Override
    public Mono<Long> clear() {
        return itemsCountCacheTemplate.delete(itemsCountCacheTemplate.keys(KEY_PREFIX + ':' + '*'));
    }

    private static @NonNull String buildKey(String search, Integer pageNumber, Integer pageSize) {
        return String.join(KEY_DELIMITER, KEY_PREFIX, search, pageNumber.toString(), pageSize.toString());
    }
}
