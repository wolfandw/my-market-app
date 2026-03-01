package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.OrderItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

/**
 * Репозиторий для работы со строками заказов.
 */
@Repository
public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
    /**
     * Возвращает список строк по идентификатору заказа.
     *
     * @param orderId идентификатор заказа
     * @return список строк заказ.
     */
    Flux<OrderItem> findAllByOrderId(Long orderId);
}
