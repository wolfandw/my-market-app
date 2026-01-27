package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.repository.*;
import io.github.wolfandw.mymarket.service.FileStorageService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Абстрактный модульный тест.
 */
@SpringBootTest
public abstract class AbstractServiceTest {
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
}
