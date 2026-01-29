package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.repository.*;
import io.github.wolfandw.mymarket.service.*;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Абстрактный модульный тест сервисов.
 */
@SpringBootTest
public abstract class AbstractServiceTest extends AbstractTest {
    @MockitoBean(reset = MockReset.BEFORE)
    protected ItemRepository itemRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    protected CartRepository cartRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    protected CartItemRepository cartItemRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    protected OrderRepository orderRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    protected OrderItemRepository orderItemRepository;

    @MockitoBean(reset = MockReset.BEFORE)
    protected FileStorageService fileStorageService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected EntityImageService entityImageService;

    @Autowired
    protected CartService cartService;

    @Autowired
    protected OrderService orderService;
}
