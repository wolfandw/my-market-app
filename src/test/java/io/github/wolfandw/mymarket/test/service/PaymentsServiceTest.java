package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Модульный тест сервиса платежей.
 */
public class PaymentsServiceTest extends AbstractServiceTest {
    @Test
    void getBalanceTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(paymentsApi.getBalance(eq(id))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.getBalance(id)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    void getBalanceErrorTest() {
        Long id = 1L;
        when(paymentsApi.getBalance(eq(id))).thenReturn(Mono.empty());
        StepVerifier.create(paymentsService.getBalance(id)).verifyComplete();
    }

    @Test
    void makePaymentTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.makePayment(id, mockPaymentDto)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    void makePaymentBadBalanceTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(false);

        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.makePayment(id, mockPaymentDto)).verifyComplete();
    }

    @Test
    void makePaymentServiceErrorTest() {
        Long id = 1L;
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(paymentsService.makePayment(id, mockPaymentDto)).verifyComplete();
    }

    @Test
    void topUpBalanceTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(id), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.topUpBalance(id, mockReceiptDto)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    void topUpBalanceServiceErrorTest() {
        Long id = 1L;
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(id), any(ReceiptDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(paymentsService.topUpBalance(id, mockReceiptDto)).verifyComplete();
    }
}
