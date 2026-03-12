package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.model.*;
import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
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

    @Test
    @IsRoleUser
    public void buyUserUserTest() {
        buyTest(getUser(), getUserMono());
    }

    @Test
    @IsRoleGuest
    public void buyUserGuestTest() {
        trxStepVerifier.create(buyService.buy()).verifyError(AuthorizationDeniedException.class);
    }

    private void buyTest(User testUser, Mono<User> testUserMono) {
        Cart cart = CARTS.get(testUser.getId());
        when(cartRepository.findFirstByUserId(testUser.getId())).thenReturn(Mono.just(cart));
        when(cartRepository.save(cart)).thenReturn(Mono.just(cart));
        Flux<CartItem> cartItemsFlux = Flux.fromStream(CART_ITEMS.get(cart.getId()).values().stream());
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(cartItemsFlux);
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);

        BigDecimal totalBefore = cart.getTotal();
        Assertions.assertThat(totalBefore).isEqualTo(BigDecimal.valueOf(7815));

        Order order = new Order();
        order.setId(3L);
        order.setUserId(testUser.getId());
        order.setTotalSum(cart.getTotal());
        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
        Flux<OrderItem> orderItemsFlux = cartItemsFlux.map(cartItem ->
                new OrderItem(order.getId(), cartItem.getItemId(), cartItem.getCount()));
        when(orderItemRepository.saveAll(any(Flux.class))).thenReturn(orderItemsFlux);
        when(cartItemRepository.deleteAllByCartId(cart.getId())).thenReturn(Mono.empty());

        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(testUser.getId());
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(mockPaymentsService.makeUserPayment(any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(buyService.buy()).
                consumeNextWith(orderDto -> {
                    assertThat(orderDto.totalSum()).isEqualTo(7815L);
                }).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void buyUserBadBalanceOrPaymentsServerErrorUserTest() {
        buyBadBalanceOrPaymentsServerErrorTest(getUser(), getUserMono());
    }

    @Test
    @IsRoleGuest
    public void buyUserBadBalanceOrPaymentsServerErrorGuestTest() {
        trxStepVerifier.create(buyService.buy()).verifyError(AuthorizationDeniedException.class);
    }

    private void buyBadBalanceOrPaymentsServerErrorTest(User testUser, Mono<User> testUserMono) {
        Cart cart = CARTS.get(testUser.getId());
        when(cartRepository.findFirstByUserId(testUser.getId())).thenReturn(Mono.just(cart));
        Flux<CartItem> cartItemsFlux = Flux.fromIterable(CART_ITEMS.get(cart.getId()).values().stream().toList());
        when(cartItemRepository.findAllByCartId(cart.getId())).thenReturn(cartItemsFlux);

        BigDecimal totalBefore = cart.getTotal();
        Assertions.assertThat(totalBefore).isEqualTo(BigDecimal.valueOf(7815));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);

        when(mockPaymentsService.makeUserPayment(any(PaymentDto.class))).thenReturn(Mono.empty());

        trxStepVerifier.create(buyService.buy()).verifyComplete();

        verify(cartRepository, never()).save(cart);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(any(Flux.class));
        verify(cartItemRepository, never()).deleteAllByCartId(cart.getId());
    }
}
