package io.github.wolfandw.mymarket.itest;

import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.CartRepository;
import io.github.wolfandw.mymarket.repository.ItemRepository;
import io.github.wolfandw.mymarket.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

/**
 * Абстрактный интеграционный тест.
 */
public abstract class AbstractIntegrationTest {
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
}
