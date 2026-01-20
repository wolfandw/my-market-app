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
    Page<Item> findByTitleContainingOrDescriptionContainingAllIgnoreCase(String title, String description, Pageable pageable);
}
