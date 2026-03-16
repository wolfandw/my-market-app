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
public abstract class AbstractTest extends AbstractSecurityTest{
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
        super.setUp();

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
        CART_ITEMS.clear();

        // admin cart
        Cart adminCart = new Cart();
        adminCart.setId(getAdmin().getId());
        adminCart.setUserId(getAdmin().getId());
        adminCart.setTotal(BigDecimal.valueOf(7815));
        CARTS.put(adminCart.getId(), adminCart);
        List<CartItem> adminCartItems = new ArrayList<>();
        adminCartItems.add(new CartItem(1L, adminCart.getId(), 2L, 60));
        adminCartItems.add(new CartItem(2L, adminCart.getId(), 3L, 50));
        adminCartItems.add(new CartItem(3L, adminCart.getId(), 4L, 40));
        adminCartItems.add(new CartItem(4L, adminCart.getId(), 5L, 30));
        adminCartItems.add(new CartItem(5L, adminCart.getId(), 6L, 20));
        adminCartItems.add(new CartItem(6L, adminCart.getId(), 7L, 10));
        adminCartItems.add(new CartItem(7L, adminCart.getId(), 8L, 130));
        adminCartItems.add(new CartItem(8L, adminCart.getId(), 9L, 120));
        adminCartItems.add(new CartItem(9L, adminCart.getId(), 10L, 110));
        adminCartItems.add(new CartItem(10L, adminCart.getId(), 11L, 100));
        adminCartItems.add(new CartItem(11L, adminCart.getId(), 12L, 90));
        adminCartItems.add(new CartItem(12L, adminCart.getId(), 13L, 80));
        CART_ITEMS.put(adminCart.getId(), adminCartItems.stream().collect(Collectors.toMap(CartItem::getItemId, Function.identity())));

        // user cart
        Cart userCart = new Cart();
        userCart.setId(getUser().getId());
        userCart.setUserId(getUser().getId());
        userCart.setTotal(BigDecimal.valueOf(7815));
        CARTS.put(userCart.getId(), userCart);
        List<CartItem> userCartItems = new ArrayList<>();
        userCartItems.add(new CartItem(1L, userCart.getId(), 2L, 60));
        userCartItems.add(new CartItem(2L, userCart.getId(), 3L, 50));
        userCartItems.add(new CartItem(3L, userCart.getId(), 4L, 40));
        userCartItems.add(new CartItem(4L, userCart.getId(), 5L, 30));
        userCartItems.add(new CartItem(5L, userCart.getId(), 6L, 20));
        userCartItems.add(new CartItem(6L, userCart.getId(), 7L, 10));
        userCartItems.add(new CartItem(7L, userCart.getId(), 8L, 130));
        userCartItems.add(new CartItem(8L, userCart.getId(), 9L, 120));
        userCartItems.add(new CartItem(9L, userCart.getId(), 10L, 110));
        userCartItems.add(new CartItem(10L, userCart.getId(), 11L, 100));
        userCartItems.add(new CartItem(11L, userCart.getId(), 12L, 90));
        userCartItems.add(new CartItem(12L, userCart.getId(), 13L, 80));
        CART_ITEMS.put(userCart.getId(), userCartItems.stream().collect(Collectors.toMap(CartItem::getItemId, Function.identity())));

        ORDERS.clear();
        ORDER_ITEMS.clear();

        // admin order
        Order adminOrder = new Order();
        adminOrder.setId(getAdmin().getId());
        adminOrder.setUserId(getAdmin().getId());
        adminOrder.setTotalSum(BigDecimal.valueOf(8129));
        ORDERS.put(adminOrder.getId(), adminOrder);
        List<OrderItem> adminOrderItems = new ArrayList<>();
        adminOrderItems.add(new OrderItem(1L, adminOrder.getId(), 2L, 65));
        adminOrderItems.add(new OrderItem(2L, adminOrder.getId(), 3L, 55));
        adminOrderItems.add(new OrderItem(3L, adminOrder.getId(), 4L, 45));
        adminOrderItems.add(new OrderItem(4L, adminOrder.getId(), 5L, 35));
        adminOrderItems.add(new OrderItem(5L, adminOrder.getId(), 6L, 25));
        adminOrderItems.add(new OrderItem(6L, adminOrder.getId(), 7L,15));
        adminOrderItems.add(new OrderItem(7L, adminOrder.getId(), 8L, 135));
        adminOrderItems.add(new OrderItem(8L, adminOrder.getId(), 9L, 125));
        adminOrderItems.add(new OrderItem(9L, adminOrder.getId(), 10L,115));
        adminOrderItems.add(new OrderItem(10L, adminOrder.getId(), 11L, 105));
        adminOrderItems.add(new OrderItem(11L, adminOrder.getId(), 12L, 95));
        adminOrderItems.add(new OrderItem(12L, adminOrder.getId(), 13L, 85));
        ORDER_ITEMS.put(adminOrder.getId(), adminOrderItems.stream().collect(Collectors.toMap(OrderItem::getItemId, Function.identity())));

        // user order
        Order userOrder = new Order();
        userOrder.setId(getUser().getId());
        userOrder.setUserId(getUser().getId());
        userOrder.setTotalSum(BigDecimal.valueOf(8129));
        ORDERS.put(userOrder.getId(), userOrder);
        List<OrderItem> userOrderItems = new ArrayList<>();
        userOrderItems.add(new OrderItem(1L, userOrder.getId(), 2L, 65));
        userOrderItems.add(new OrderItem(2L, userOrder.getId(), 3L, 55));
        userOrderItems.add(new OrderItem(3L, userOrder.getId(), 4L, 45));
        userOrderItems.add(new OrderItem(4L, userOrder.getId(), 5L, 35));
        userOrderItems.add(new OrderItem(5L, userOrder.getId(), 6L, 25));
        userOrderItems.add(new OrderItem(6L, userOrder.getId(), 7L, 15));
        userOrderItems.add(new OrderItem(7L, userOrder.getId(), 8L, 135));
        userOrderItems.add(new OrderItem(8L, userOrder.getId(), 9L, 125));
        userOrderItems.add(new OrderItem(9L, userOrder.getId(), 10L,115));
        userOrderItems.add(new OrderItem(10L, userOrder.getId(), 11L, 105));
        userOrderItems.add(new OrderItem(11L, userOrder.getId(), 12L, 95));
        userOrderItems.add(new OrderItem(12L, userOrder.getId(), 13L, 85));
        ORDER_ITEMS.put(userOrder.getId(), userOrderItems.stream().collect(Collectors.toMap(OrderItem::getItemId, Function.identity())));
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
