package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.CartItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Репозиторий для работы со строками корзин.
 */
@Repository
public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
    /**
     * Возвращает строку корзины с фильтром по идентификатору товара.
     *
     * @param cartId идентификатор корзины
     * @param itemId фильтр по идентификатору товара
     * @return строка корзины с фильтром по идентификатору корзины и товара.
     */
    Mono<CartItem> findByCartIdAndItemId(Long cartId, Long itemId);

    /**
     * Возвращает список строк корзины.
     *
     * @param cartId идентификатор корзины
     * @return список строк корзины.
     */
    Flux<CartItem> findAllByCartId(Long cartId);

    /**
     * Удаляет строки из корзины.
     *
     * @param cartId идентификатор корзины
     */
    Mono<Void> deleteAllByCartId(Long cartId);
}
