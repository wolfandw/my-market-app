package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Item;
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
     * Возвращает поток товаров отобранных по названию или описанию.
     * Поток может быть отсортирован по названию, цене и не отсортирован.
     *
     * @param title подстрока поиска в наименовании
     * @param description подстрока поиска в описании
     * @param sort параметры сортировки
     * @return поток товаров, отобранных по названию или описанию и сортировкой (опционально)
     */
    Flux<Item> findByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description, Sort sort);

    /**
     * Возвращает количество товаров отобранных по названию или описанию.
     *
     * @param title подстрока поиска в наименовании
     * @param description подстрока поиска в описании
     * @return количество товаров, отобранных по названию или описанию и сортировкой (опционально)
     */
    Mono<Long> countByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description);
}
