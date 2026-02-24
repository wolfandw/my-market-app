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
     * @param sort параметры сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return количество товаров для пейджинга из кэша
     */
    Mono<Long> getItemsCount(String search, String sort, Integer pageNumber, Integer pageSize);

    /**
     * Помещает количество товаров для пейджинга в кэш.
     *
     * @param search параметры поиска
     * @param sort параметры сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @param count количество товаров для пейджинга
     * @return {@code true} если Ок
     */
    Mono<Boolean> cache(String search, String sort, Integer pageNumber, Integer pageSize, Long count);
}
