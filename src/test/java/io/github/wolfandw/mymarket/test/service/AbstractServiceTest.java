package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.model.*;
import io.github.wolfandw.mymarket.repository.*;
import io.github.wolfandw.mymarket.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Map;
import java.util.TreeMap;

/**
 * Абстрактный модульный тест.
 */
@SpringBootTest
public abstract class AbstractServiceTest {
    public static Map<Long, Item> ITEMS = new TreeMap<>();
    public static Map<Long, Cart> CARTS = new TreeMap<>();
    public static Map<Long, Order> ORDERS = new TreeMap<>();

    @BeforeEach
    void setUp() {
        ITEMS.clear();
        ITEMS.put(1L, new Item(1L, "Item 07 SearchTag", "item 07 description", "1.png", BigDecimal.valueOf(7)));
        ITEMS.put(2L, new Item(2L, "Item 08", "item 08 description", "2.png", BigDecimal.valueOf(6)));
        ITEMS.put(3L, new Item(3L, "Item 09", "item 09 description", "3.png", BigDecimal.valueOf(5)));
        ITEMS.put(4L, new Item(4L, "Item 10", "item 10 description SearchTag", "4.png", BigDecimal.valueOf(4)));
        ITEMS.put(5L, new Item(5L, "Item 11", "item 11 description", "5.png", BigDecimal.valueOf(3)));
        ITEMS.put(6L, new Item(6L, "Item 12", "item 12 description", "6.png", BigDecimal.valueOf(2)));
        ITEMS.put(7L, new Item(7L, "Item 13", "item 13 description", "7.png", BigDecimal.valueOf(1)));
        ITEMS.put(8L, new Item(8L, "Item 01 searchtag", "item 01 description", "8.png", BigDecimal.valueOf(13)));
        ITEMS.put(9L, new Item(9L, "Item 02", "item 02 description", "9.png", BigDecimal.valueOf(12)));
        ITEMS.put(10L, new Item(10L, "Item 03", "item 03 description", "10.png", BigDecimal.valueOf(11)));
        ITEMS.put(11L, new Item(11L, "Item 04", "item 04 description", "11.png", BigDecimal.valueOf(10)));
        ITEMS.put(12L, new Item(12L, "Item 05", "item 05 description", "12.png", BigDecimal.valueOf(9)));
        ITEMS.put(13L, new Item(13L, "Item 06", "item 06 description searchtag", "13.png", BigDecimal.valueOf(8)));

        CARTS.clear();
        Cart cart = new Cart(1L);
        cart.setTotal(BigDecimal.valueOf(7700));
        cart.getItems().add(new CartItem(1L, cart, ITEMS.get(2L), 60));
        cart.getItems().add(new CartItem(2L, cart, ITEMS.get(3L), 50));
        cart.getItems().add(new CartItem(3L, cart, ITEMS.get(4L), 40));
        cart.getItems().add(new CartItem(4L, cart, ITEMS.get(5L), 30));
        cart.getItems().add(new CartItem(5L, cart, ITEMS.get(6L), 20));
        cart.getItems().add(new CartItem(6L, cart, ITEMS.get(7L), 10));
        cart.getItems().add(new CartItem(7L, cart, ITEMS.get(8L), 130));
        cart.getItems().add(new CartItem(8L, cart, ITEMS.get(9L), 120));
        cart.getItems().add(new CartItem(9L, cart, ITEMS.get(10L), 110));
        cart.getItems().add(new CartItem(10L, cart, ITEMS.get(11L), 100));
        cart.getItems().add(new CartItem(11L, cart, ITEMS.get(12L), 90));
        cart.getItems().add(new CartItem(12L, cart, ITEMS.get(13L), 80));
        CARTS.put(1L, cart);

        ORDERS.clear();
        Order order = new Order(1L);
        order.setTotalSum(BigDecimal.valueOf(8120));
        order.getItems().add(new OrderItem(1L, order, ITEMS.get(2L), 65));
        order.getItems().add(new OrderItem(2L, order, ITEMS.get(3L), 55));
        order.getItems().add(new OrderItem(3L, order, ITEMS.get(4L), 45));
        order.getItems().add(new OrderItem(4L, order, ITEMS.get(5L), 35));
        order.getItems().add(new OrderItem(5L, order, ITEMS.get(6L), 25));
        order.getItems().add(new OrderItem(6L, order, ITEMS.get(7L), 15));
        order.getItems().add(new OrderItem(7L, order, ITEMS.get(8L), 135));
        order.getItems().add(new OrderItem(8L, order, ITEMS.get(9L), 125));
        order.getItems().add(new OrderItem(9L, order, ITEMS.get(10L), 115));
        order.getItems().add(new OrderItem(10L, order, ITEMS.get(11L), 105));
        order.getItems().add(new OrderItem(11L, order, ITEMS.get(12L), 95));
        order.getItems().add(new OrderItem(12L, order, ITEMS.get(13L), 85));
        ORDERS.put(1L, order);
    }

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
