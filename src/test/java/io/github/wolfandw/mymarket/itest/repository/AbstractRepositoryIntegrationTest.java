package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;

/**
 * Абстрактный интеграционный тест репозиториев.
 */
@DataR2dbcTest
public abstract class AbstractRepositoryIntegrationTest {
    /**
     * Идентификатор корзины по умолчанию.
     */
    protected static final Long DEFAULT_CART_ID = 1L;

    /**
     * Репозиторий товаров.
     */
    @Autowired
    protected ItemRepository itemRepository;

    /**
     * Репозиторий корзин.
     */
    @Autowired
    protected CartRepository cartRepository;

    /**
     * Репозиторий строк корзин.
     */
    @Autowired
    protected CartItemRepository cartItemRepository;

    /**
     * Репозиторий заказов.
     */
    @Autowired
    protected OrderRepository orderRepository;

    /**
     * Репозиторий строк заказа.
     */
    @Autowired
    protected OrderItemRepository orderItemRepository;
}
