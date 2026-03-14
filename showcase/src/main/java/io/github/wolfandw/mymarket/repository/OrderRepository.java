package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Order;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Репозиторий для работы с заказами.
 */
@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
    /**
     * Возвращает список заказов пользователя.
     *
     * @param userId идентификатор пользователя
     * @return  список заказов пользователя
     */
    Flux<Order> findAllByUserId(Long userId);

    /**
     * Возвращает заказ пользователя.
     *
     * @param orderId идентификатор заказа
     * @param userId идентификатор пользователя
     * @return  заказ пользователя
     */
    Mono<Order> findByIdAndUserId(Long orderId, Long userId);
}

