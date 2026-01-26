package io.github.wolfandw.mymarket.test;

import io.github.wolfandw.mymarket.repository.*;
import io.github.wolfandw.mymarket.service.FileStorageService;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Абстрактный модульный тест.
 */
@SpringBootTest
public abstract class AbstractTest {
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
