package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты сервиса покупок.
 */
public class BuyServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    void buyOrderTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID)).
                consumeNextWith(newOrderDto -> {
                    assertThat(newOrderDto.totalSum()).isEqualTo(7808L);
                }).verifyComplete();
    }

    @Test
    void buyOrderLowBalanceTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(false);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID)).verifyComplete();
    }

    @Test
    void buyOrderPaymentsServiceErrorTest() {
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID)).verifyComplete();
    }

    @Test
    void buyOrderItemsTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID).flatMapMany(orderDto ->
                        orderService.getOrderItems(orderDto.id()).collectList())).
                assertNext(newOrderItems -> {
                    Assertions.assertThat(newOrderItems).isNotEmpty();
                    Assertions.assertThat(newOrderItems.size()).isEqualTo(12);
                }).verifyComplete();
    }

    @Test
    void buyCartTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(cartService.getCart(DEFAULT_CART_ID)).
                consumeNextWith(actualCartBefore -> {
                    Assertions.assertThat(actualCartBefore.total()).isEqualTo(7808L);
                }).verifyComplete();

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID).flatMap(orderDto ->
                        cartService.getCart(DEFAULT_CART_ID))).
                consumeNextWith(actualCartAfter -> {
                    Assertions.assertThat(actualCartAfter.total()).isEqualTo(0L);
                }).verifyComplete();
    }

    @Test
    void buyCartItemsTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(DEFAULT_CART_ID);
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(cartService.getCartItems(DEFAULT_CART_ID).collectList()).
                assertNext(actualCartItemsAfter -> {
                    Assertions.assertThat(actualCartItemsAfter).isNotEmpty();
                    Assertions.assertThat(actualCartItemsAfter.size()).isEqualTo(12);
                }).verifyComplete();

        trxStepVerifier.create(buyService.buy(DEFAULT_CART_ID).flatMapMany(orderDto ->
                        cartService.getCartItems(DEFAULT_CART_ID).collectList())).
                assertNext(actualCartItemsAfter -> {
                    Assertions.assertThat(actualCartItemsAfter).isEmpty();
                }).verifyComplete();
    }
}
