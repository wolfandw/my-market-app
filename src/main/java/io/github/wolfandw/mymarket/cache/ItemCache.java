package io.github.wolfandw.mymarket.cache;

import io.github.wolfandw.mymarket.model.Item;
import reactor.core.publisher.Mono;

/**
 * Кэш товаров.
 */
public interface ItemCache {
    /**
     * Получить товар из кэша.
     *S
     * @param id идентификатор товара
     * @return товар из кэша
     */
    Mono<Item> getItem(Long id);

    /**
     * Поместить товар в кэш.
     *
     * @param id идентификатор товара
     * @param item товар
     * @return {@code true} если Ок
     */
    Mono<Boolean> cache(Long id, Item item);
}
