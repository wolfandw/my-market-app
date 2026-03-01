package io.github.wolfandw.payment.test.service;

import io.github.wolfandw.payment.service.PaymentsService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Модульный тест сервиса платежей.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentsServiceTest {
    @Autowired
    private PaymentsService paymentsService;

    @Test
    @Order(1)
    void topUpBalanceTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.topUpBalance(id, BigDecimal.TEN)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @Order(2)
    void topUpBalanceNullTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.topUpBalance(id, null)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                }).verifyComplete();
    }

    @Test
    @Order(3)
    void topUpBalanceNegativeTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.topUpBalance(id, BigDecimal.valueOf(-1L))).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                }).verifyComplete();
    }

    @Test
    @Order(4)
    void getBalanceTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.getBalance(id)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @Order(5)
    void makePaymentTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.makePayment(id, BigDecimal.TEN)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @Order(6)
    void makePaymentLowBalanceTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.makePayment(id, BigDecimal.TEN)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                }).verifyComplete();
    }

    @Test
    @Order(6)
    void makePaymentNullTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.makePayment(id, null)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);

                }).verifyComplete();
    }
    @Test
    @Order(6)
    void makePaymentNegativeTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.makePayment(id, BigDecimal.valueOf(-1L))).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                }).verifyComplete();
    }
}
