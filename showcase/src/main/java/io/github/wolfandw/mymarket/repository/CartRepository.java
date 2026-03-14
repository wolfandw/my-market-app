package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Cart;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Репозиторий для работы с корзинами.
 */
@Repository
public interface CartRepository extends R2dbcRepository<Cart, Long> {
    /**
     * Возвращает корзину по идентификатору пользователя.
     *
     * @param userId идентификатор пользователя
     * @return  корзина по идентификатору пользователя
     */
    Mono<Cart> findFirstByUserId(Long userId);
}
