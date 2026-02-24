package io.github.wolfandw.mymarket.cache;

import io.github.wolfandw.mymarket.model.Item;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Кэш списка товаров.
 */
public interface ItemsCache {
    /**
     * Получает список товаров из кэша.
     *
     * @param search параметры поиска
     * @param sort параметры сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return список товаров из кэша
     */
    Flux<Item> getItems(String search, String sort, Integer pageNumber, Integer pageSize);

    /**
     * Помещает список товаров в кэш.
     *
     * @param search параметры поиска
     * @param sort параметры сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @param databaseItems список товаров из репозитория
     * @return список товаров из репозитория
     */
    Flux<Item> cache(String search, String sort, Integer pageNumber, Integer pageSize, Flux<Item> databaseItems);

    /**
     * Очищает кэш списка товаров.
     *
     * @return количество удаленных записей
     */
    Mono<Long> clear();
}
