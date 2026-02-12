package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.itest.configuration.TrxStepVerifier;
import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.repository.*;
import io.github.wolfandw.mymarket.service.*;
import io.github.wolfandw.mymarket.test.AbstractTest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

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

    @Autowired
    protected BuyService buyService;

    @Autowired
    protected TrxStepVerifier trxStepVerifier;

    /**
     * Мокает чтение строки корзины по идентификатору корзины и товара.
     */
    protected void mockCartItem() {
        doAnswer(new Answer<Mono<CartItem>>() {
            @Override
            public Mono<CartItem> answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length == 2 && arguments[0] != null && arguments[1] != null) {
                    Long cartId = (Long) arguments[0];
                    Long itemId = (Long) arguments[1];
                    CartItem cartItem = CART_ITEMS.get(cartId).get(itemId);
                    return cartItem == null ? Mono.empty() : Mono.just(cartItem);
                }
                return Mono.empty();
            }
        }).when(cartItemRepository).findByCartIdAndItemId(any(Long.class), any(Long.class));
    }

    protected void mockCart() {
        doAnswer(new Answer<Mono<Cart>>() {
            @Override
            public Mono<Cart> answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length == 1 && arguments[0] != null) {
                    Long cartId = (Long) arguments[0];
                    Cart cart = CARTS.get(cartId);
                    return cart == null ? Mono.empty() : Mono.just(cart);
                }
                return Mono.empty();
            }
        }).when(cartRepository).findById(any(Long.class));
    }

    /**
     * Мокает чтение товара по идентификатору.
     */
    protected void mockItem() {
        doAnswer(new Answer<Mono<Item>>() {
            @Override
            public Mono<Item> answer(InvocationOnMock invocation) throws Throwable {
                Object[] arguments = invocation.getArguments();
                if (arguments != null && arguments.length == 1 && arguments[0] != null) {
                    Long itemId = (Long) arguments[0];
                    Item item = ITEMS.get(itemId);
                    return item == null ? Mono.empty() : Mono.just(item);
                }
                return Mono.empty();
            }
        }).when(itemRepository).findById(any(Long.class));
    }
}
