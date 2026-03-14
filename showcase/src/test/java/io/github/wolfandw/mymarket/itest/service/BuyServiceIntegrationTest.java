package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
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
    @IsRoleUser
    public void buyOrderUserTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(getUser().getId());
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.ZERO);
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(buyService.buy()).
                consumeNextWith(newOrderDto -> {
                    assertThat(newOrderDto.totalSum()).isEqualTo(7808L);
                }).verifyComplete();
    }

    @Test
    public void buyOrderTest() {
        trxStepVerifier.create(buyService.buy()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void buyOrderLowBalanceUserTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(getUser().getId());
        balanceDto.setAccept(false);
        balanceDto.setBalance(BigDecimal.ZERO);
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(buyService.buy()).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void buyOrderPaymentsServiceErrorUserTest() {
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        trxStepVerifier.create(buyService.buy()).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void buyOrderItemsUserTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(getUser().getId());
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.ZERO);
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(buyService.buy().flatMapMany(orderDto ->
                        orderService.getUserOrderItems(orderDto.id()).collectList())).
                assertNext(newOrderItems -> {
                    Assertions.assertThat(newOrderItems).isNotEmpty();
                    Assertions.assertThat(newOrderItems.size()).isEqualTo(12);
                }).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void buyCartUserTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(getUser().getId());
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.ZERO);
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(cartService.getUserCart()).
                consumeNextWith(actualCartBefore -> {
                    Assertions.assertThat(actualCartBefore.total()).isEqualTo(7808L);
                }).verifyComplete();

        trxStepVerifier.create(buyService.buy().flatMap(orderDto ->
                        cartService.getUserCart())).
                consumeNextWith(actualCartAfter -> {
                    Assertions.assertThat(actualCartAfter.total()).isEqualTo(0L);
                }).verifyComplete();
    }

    @Test
    @IsRoleUser
    void buyCartItemsUserTest() {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setId(getUser().getId());
        balanceDto.setAccept(true);
        balanceDto.setBalance(BigDecimal.valueOf(0L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(balanceDto));

        trxStepVerifier.create(cartService.getUserCartItems().collectList()).
                assertNext(actualCartItemsAfter -> {
                    Assertions.assertThat(actualCartItemsAfter).isNotEmpty();
                    Assertions.assertThat(actualCartItemsAfter.size()).isEqualTo(12);
                }).verifyComplete();

        trxStepVerifier.create(buyService.buy().flatMapMany(orderDto ->
                        cartService.getUserCartItems().collectList())).
                assertNext(actualCartItemsAfter -> {
                    Assertions.assertThat(actualCartItemsAfter).isEmpty();
                }).verifyComplete();
    }
}
