package io.github.wolfandw.mymarket.cache.impl;

import io.github.wolfandw.mymarket.cache.EntityImageCache;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Реализация {@link EntityImageCache}
 */
@Component
public class EntityImageCacheImpl implements EntityImageCache {
    private static final Logger LOG = LoggerFactory.getLogger(EntityImageCacheImpl.class);
    private static String KEY_PREFIX = "mymarket:items:image";
    private static final String KEY_DELIMITER = ":";

    private final ReactiveRedisTemplate<String, byte[]> entityImageCacheTemplate;

    @Value("${mymarket.redis.time-to-live}")
    private Integer timeToLive;

    /**
     * Создает новый кэш изображений товаров.
     *
     * @param entityImageCacheTemplate кэш изображений товаров
     */
    public EntityImageCacheImpl(ReactiveRedisTemplate<String, byte[]> entityImageCacheTemplate) {
        this.entityImageCacheTemplate = entityImageCacheTemplate;
    }

    @Override
    public Mono<byte[]> getEntityImage(Long entityId) {
        String key = buildKey(entityId);
        return entityImageCacheTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<byte[]> cache(Long entityId, Mono<byte[]> databaseContent) {
        String key = buildKey(entityId);
        return databaseContent
                .flatMap(content -> {
                            LOG.info("Помещаем в кэш картинку сущности");
                            return entityImageCacheTemplate.opsForValue()
                                    .set(key, content, Duration.ofMinutes(timeToLive))
                                    .thenReturn(content);
                        }
                );
    }

    @Override
    public Mono<Long> clear(Long entityId) {
        return entityImageCacheTemplate.delete(buildKey(entityId));
    }

    private static @NonNull String buildKey(Long entityId) {
        return String.join(KEY_DELIMITER , KEY_PREFIX, entityId.toString());
    }
}
