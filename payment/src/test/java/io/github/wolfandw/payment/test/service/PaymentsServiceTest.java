package io.github.wolfandw.payment.test.service;

import io.github.wolfandw.payment.service.PaymentsService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
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
    void topUpBalanceIsUnauthorizedTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.topUpBalance(id, BigDecimal.TEN)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
    @Order(3)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
    @Order(4)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
    @Order(5)
    void getBalanceIsUnauthorizedTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.getBalance(id)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @Order(6)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
    @Order(7)
    void makePaymentIsUnauthorizedTest() {
        Long id = 1L;
        StepVerifier.create(paymentsService.makePayment(id, BigDecimal.TEN)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @Order(8)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
    @Order(9)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
    @Order(10)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
    @Order(11)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
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
