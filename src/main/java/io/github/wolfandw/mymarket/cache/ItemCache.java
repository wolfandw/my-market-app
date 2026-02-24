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
     * @param itemId идентификатор товара
     * @return товар из кэша
     */
    Mono<Item> getItem(Long itemId);

    /**
     * Поместить товар в кэш.
     *
     * @param databaseItem товар из репозитория
     * @return товар из репозитория
     */
    Mono<Item> cache(Mono<Item> databaseItem);

    /**
     * Очищает кэш товара.
     *
     * @param itemId идентификатор товара
     * @return количество удаленных записей
     */
    Mono<Long> clear(Long itemId);
}
