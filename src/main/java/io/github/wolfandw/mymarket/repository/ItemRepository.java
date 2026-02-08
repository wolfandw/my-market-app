package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Репозиторий для работы с товарами.
 */
@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {
    /**
     * Возвращает поток товаров на страницу.
     *
     * @param pageable параметры страницы и сортировки
     * @return поток товаров на страницу
     */
    Flux<Item> findAllBy(Pageable pageable);

    /**
     * Возвращает поток товаров на страницу, отобранных по названию или описанию.
     * Поток может быть отсортирован по названию, цене и не отсортирован.
     *
     * @param title подстрока поиска в наименовании
     * @param description подстрока поиска в описании
     * @param pageable параметры страницы и сортировки
     * @return поток товаров на страницу, отобранных по названию или описанию и сортировкой (опционально)
     */
    Flux<Item> findByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description, Pageable pageable);

    /**
     * Возвращает общее количество товаров, отобранных по названию или описанию.
     *
     * @param title подстрока поиска в наименовании
     * @param description подстрока поиска в описании
     * @return общее количество товаров, отобранных по названию или описанию и сортировкой (опционально)
     */
    Mono<Long> countByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description);
}
