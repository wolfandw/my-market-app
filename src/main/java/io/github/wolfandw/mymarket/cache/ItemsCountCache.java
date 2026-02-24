package io.github.wolfandw.mymarket.cache;

import reactor.core.publisher.Mono;

/**
 * Кэш количества товаров для пейджинга.
 */
public interface ItemsCountCache {
    /**
     * Получает количество товаров для пейджинга из кэша.
     *
     * @param search параметры поиска
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return количество товаров для пейджинга из кэша
     */
    Mono<Long> getItemsCount(String search, Integer pageNumber, Integer pageSize);

    /**
     * Помещает количество товаров для пейджинга в кэш.
     *
     * @param search параметры поиска
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @param databaseCount количество товаров для пейджинга из репозитория
     * @return количество товаров для пейджинга из репозитория
     */
    Mono<Long> cache(String search, Integer pageNumber, Integer pageSize, Mono<Long> databaseCount);

    /**
     * Очищает кэш количества товаров.
     *
     * @return количество удаленных записей
     */
    Mono<Long> clear();
}
