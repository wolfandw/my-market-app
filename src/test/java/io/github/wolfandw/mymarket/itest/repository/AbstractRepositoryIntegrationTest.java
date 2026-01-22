package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

/**
 * Абстрактный интеграционный тест репозиториев.
 */
@DataJpaTest
public abstract class AbstractRepositoryIntegrationTest {
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
