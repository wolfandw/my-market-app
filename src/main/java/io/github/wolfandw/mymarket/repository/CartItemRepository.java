package io.github.wolfandw.mymarket.repository;

import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы со строками корзин.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    /**
     * Возвращает список строк корзины.
     *
     * @param cart корзина
     * @return список строк корзины.
     */
    List<CartItem> findAllByCart(Cart cart);

    /**
     * Возвращает список строк корзины с фильтром по товарам.
     *
     * @param cart корзина
     * @param items фильтр по товарам
     * @return список строк корзины с фильтром по товарам.
     */
    List<CartItem> findAllByCartAndItemIn(Cart cart, Collection<Item> items);

    /**
     * Возвращает список строк корзины с фильтром по идентификатору товара.
     *
     * @param cart корзина
     * @param itemId фильтр по идентификатору товара
     * @return строка корзины с фильтром по идентификатору товара.
     */
    Optional<CartItem> findByCartAndItemId(Cart cart, Long itemId);
}
