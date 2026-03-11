package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.model.Cart;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Order;
import io.github.wolfandw.mymarket.model.OrderItem;
import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.mymarket.service.UserService;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Модульные тесты сервиса покупок.
 */
public class BuyServiceTest extends AbstractServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private PaymentsService mockPaymentsService;

    @MockitoBean(reset = MockReset.BEFORE)
    private UserService mockUserService;

    private void prepareByTest() {
        Long cartId = 1L;
        Long userId = 1L;
        Cart cart = CARTS.get(1L);
        assert cart != null;
        when(cartRepository.findFirstByUserId(userId)).thenReturn(Mono.just(cart));
        when(cartRepository.save(cart)).thenReturn(Mono.just(cart));
        Flux<CartItem> cartItemsFlux = Flux.fromStream(CART_ITEMS.get(cartId).values().stream());
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(cartItemsFlux);
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(userId));

        BigDecimal totalBefore = cart.getTotal();
        Assertions.assertThat(totalBefore).isEqualTo(BigDecimal.valueOf(7815));

        Long orderId = 2L;
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setTotalSum(cart.getTotal());
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
        Flux<OrderItem> orderItemsFlux = cartItemsFlux.map(cartItem ->
                new OrderItem(order.getId(), cartItem.getItemId(), cartItem.getCount()));
        when(orderItemRepository.saveAll(any(Flux.class))).thenReturn(orderItemsFlux);
        when(cartItemRepository.deleteAllByCartId(cartId)).thenReturn(Mono.empty());

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(cartId);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(mockPaymentsService.makeUserPayment(any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));
    }

    @Test
    void buyIsUnauthorizedTest() {
        prepareByTest();
        trxStepVerifier.create(buyService.buy()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void buyUserTest() {
        prepareByTest();
        trxStepVerifier.create(buyService.buy()).
                consumeNextWith(orderDto -> {
                    assertThat(orderDto.totalSum()).isEqualTo(7815L);
                }).verifyComplete();
    }

    @Test
    void buyBadBalanceOrPaymentsServerErrorIsUnauthorizedTestTest() {
        Long cartId = DEFAULT_USER_ID;
        Cart cart = CARTS.get(cartId);
        assert cart != null;
        when(cartRepository.findById(cartId)).thenReturn(Mono.just(cart));
        Flux<CartItem> cartItemsFlux = Flux.fromIterable(CART_ITEMS.get(DEFAULT_USER_ID).values().stream().toList());
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(cartItemsFlux);

        BigDecimal totalBefore = cart.getTotal();
        Assertions.assertThat(totalBefore).isEqualTo(BigDecimal.valueOf(7815));

        when(mockPaymentsService.makeUserPayment(any(PaymentDto.class))).thenReturn(Mono.empty());

        trxStepVerifier.create(buyService.buy()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void buyBadBalanceOrPaymentsServerErrorUserTest() {
        Long cartId = 1L;
        Cart cart = CARTS.get(cartId);
        assert cart != null;
        when(cartRepository.findFirstByUserId(cartId)).thenReturn(Mono.just(cart));
        Flux<CartItem> cartItemsFlux = Flux.fromIterable(CART_ITEMS.get(cartId).values().stream().toList());
        when(cartItemRepository.findAllByCartId(cartId)).thenReturn(cartItemsFlux);

        BigDecimal totalBefore = cart.getTotal();
        Assertions.assertThat(totalBefore).isEqualTo(BigDecimal.valueOf(7815));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(1L));

        when(mockPaymentsService.makeUserPayment(any(PaymentDto.class))).thenReturn(Mono.empty());

        trxStepVerifier.create(buyService.buy()).verifyComplete();

        verify(cartRepository, never()).save(cart);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(any(Flux.class));
        verify(cartItemRepository, never()).deleteAllByCartId(cartId);
    }
}
