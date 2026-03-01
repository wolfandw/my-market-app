package io.github.wolfandw.mymarket.test;

import io.github.wolfandw.mymarket.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Абстрактный модульный тест.
 */
public abstract class AbstractTest {
    /**
     * Идентификатор корзины по-умолчанию.
     */
    protected static final Long DEFAULT_CART_ID = 1L;

    /**
     * Тестовые данные товаров.
     */
    public static Map<Long, Item> ITEMS = new TreeMap<>();
    /**
     * Тестовые данные корзин.
     */
    public static Map<Long, Cart> CARTS = new TreeMap<>();

    /**
     * Тестовые данные строк корзин.
     */
    public static Map<Long, Map<Long, CartItem>> CART_ITEMS = new TreeMap<>();

    /**
     * Тестовые данные заказов.
     */
    public static Map<Long, Order> ORDERS = new TreeMap<>();

    /**
     * Тестовые данные строк заказов.
     */
    public static Map<Long, Map<Long, OrderItem>> ORDER_ITEMS = new TreeMap<>();

    private static final int BUFFER_SIZE = 4096;

    /**
     * Инициализация перед каждым тестом.
     */
    @BeforeEach
    protected void setUp() {
        ITEMS.clear();
        ITEMS.put(1L, new Item(1L, "Item 07 SearchTag", "item 07 description", "1.png", new BigDecimal("7.01")));
        ITEMS.put(2L, new Item(2L, "Item 08", "item 08 description", "2.png", new BigDecimal("6.01")));
        ITEMS.put(3L, new Item(3L, "Item 09", "item 09 description", "3.png", new BigDecimal("5.01")));
        ITEMS.put(4L, new Item(4L, "Item 10", "item 10 description SearchTag", "4.png", new BigDecimal("4.01")));
        ITEMS.put(5L, new Item(5L, "Item 11", "item 11 description", "5.png", new BigDecimal("3.01")));
        ITEMS.put(6L, new Item(6L, "Item 12", "item 12 description", "6.png", new BigDecimal("2.01")));
        ITEMS.put(7L, new Item(7L, "Item 13", "item 13 description", "7.png", new BigDecimal("1.01")));
        ITEMS.put(8L, new Item(8L, "Item 01 searchtag", "item 01 description", "8.png", new BigDecimal("13.01")));
        ITEMS.put(9L, new Item(9L, "Item 02", "item 02 description", "9.png", new BigDecimal("12.01")));
        ITEMS.put(10L, new Item(10L, "Item 03", "item 03 description", "10.png", new BigDecimal("11.01")));
        ITEMS.put(11L, new Item(11L, "Item 04", "item 04 description", "11.png", new BigDecimal("10.01")));
        ITEMS.put(12L, new Item(12L, "Item 05", "item 05 description", "12.png", new BigDecimal("9.01")));
        ITEMS.put(13L, new Item(13L, "Item 06", "item 06 description searchtag", "13.png", new BigDecimal("8.01")));

        CARTS.clear();
        Cart cart = new Cart(1L);
        cart.setTotal(BigDecimal.valueOf(7815));
        CARTS.put(1L, cart);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(1L, 1L, 2L, 60));
        cartItems.add(new CartItem(2L, 1L, 3L, 50));
        cartItems.add(new CartItem(3L, 1L, 4L, 40));
        cartItems.add(new CartItem(4L, 1L, 5L, 30));
        cartItems.add(new CartItem(5L, 1L, 6L, 20));
        cartItems.add(new CartItem(6L, 1L, 7L, 10));
        cartItems.add(new CartItem(7L, 1L, 8L, 130));
        cartItems.add(new CartItem(8L, 1L, 9L, 120));
        cartItems.add(new CartItem(9L, 1L, 10L, 110));
        cartItems.add(new CartItem(10L, 1L, 11L, 100));
        cartItems.add(new CartItem(11L, 1L, 12L, 90));
        cartItems.add(new CartItem(12L, 1L, 13L, 80));
        CART_ITEMS.clear();
        CART_ITEMS.put(1L, cartItems.stream().collect(Collectors.toMap(CartItem::getItemId, Function.identity())));

        ORDERS.clear();
        Order order = new Order(1L);
        order.setTotalSum(BigDecimal.valueOf(8129));
        ORDERS.put(1L, order);
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(1L, 1L, 2L, 65));
        orderItems.add(new OrderItem(2L, 1L, 3L, 55));
        orderItems.add(new OrderItem(3L, 1L, 4L, 45));
        orderItems.add(new OrderItem(4L, 1L, 5L, 35));
        orderItems.add(new OrderItem(5L, 1L, 6L, 25));
        orderItems.add(new OrderItem(6L, 1L, 7L, 15));
        orderItems.add(new OrderItem(7L, 1L, 8L, 135));
        orderItems.add(new OrderItem(8L, 1L, 9L, 125));
        orderItems.add(new OrderItem(9L, 1L, 10L, 115));
        orderItems.add(new OrderItem(10L, 1L, 11L, 105));
        orderItems.add(new OrderItem(11L, 1L, 12L, 95));
        orderItems.add(new OrderItem(12L, 1L, 13L, 85));
        ORDER_ITEMS.clear();
        ORDER_ITEMS.put(1L, orderItems.stream().collect(Collectors.toMap(OrderItem::getItemId, Function.identity())));
    }

    /**
     * Возвращает {@link FilePart} по имени файла картинки.
     *
     * @param imageName имя файла картинки
     * @return {@link FilePart} по имени файла картинки
     */
    protected FilePart getFilePart(String imageName) {
        return new FilePart() {
            @Override
            public String name() {
                return "name";
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.EMPTY;
            }

            @Override
            public Flux<DataBuffer> content() {
                return DataBufferUtils.read(new ByteArrayResource(new byte[]{1,2,3}), new DefaultDataBufferFactory(), BUFFER_SIZE);
            }

            @Override
            public String filename() {
                return imageName;
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return DataBufferUtils.write(this.content(), dest);
            }
        };
    }
}
