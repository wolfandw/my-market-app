package io.github.wolfandw.mymarket.itest;

import io.github.wolfandw.mymarket.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Абстрактный интеграционный тест.
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {
    /**
     * Сервис товаров.
     */
    @Autowired
    protected ItemService itemService;
}
