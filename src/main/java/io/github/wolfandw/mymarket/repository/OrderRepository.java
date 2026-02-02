package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с заказами.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
