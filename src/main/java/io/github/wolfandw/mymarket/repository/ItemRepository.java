package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с товарами.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Возвращает страницу товаров отобранных по названию или описанию.
     * Список может быть отсортирован по названию, цене и не отсортирован.
     *
     * @param title подстрока поиска в наименовании
     * @param description подстрока поиска в описании
     * @param pageable параметры страницы и сортировки
     * @return страницу товаров отобранных по названию или описанию, сортировка опционально
     */
    Page<Item> findByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description, Pageable pageable);
}
