package io.github.wolfandw.mymarket.cache.impl;

import io.github.wolfandw.mymarket.cache.EntityImageCache;
import io.github.wolfandw.mymarket.cache.ItemCache;
import org.springframework.stereotype.Component;

/**
 * Реализация {@link EntityImageCache}
 */
@Component
public class EntityImageCacheImpl implements EntityImageCache {
    private static String KEY_PREFIX = "mymarket:items:image";
}
