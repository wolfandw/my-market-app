package io.github.wolfandw.payment.itest.controller;

import io.github.wolfandw.payment.itest.AbstractIntegrationTest;
import io.github.wolfandw.payment.server.domain.BalanceDto;
import io.github.wolfandw.payment.server.domain.PaymentDto;
import io.github.wolfandw.payment.server.domain.ReceiptDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

/**
 * Интеграционные тесты контроллера платежей.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PaymentsControllerIntegrationTest extends AbstractIntegrationTest {
    @Test
    @Order(1)
    void topUpBalanceIsUnauthorizedTest() {
        Long id = 1L;
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.TEN);

        webTestClient.post()
                .uri("/api/payments/1/receipt")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockReceiptDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Order(2)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void topUpBalanceTest() {
        Long id = 1L;
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.TEN);

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/payments/1/receipt")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockReceiptDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                });
    }

    @Test
    @Order(3)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void topUpBalanceNullTest() {
        Long id = 1L;
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(null);

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/payments/1/receipt")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockReceiptDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                });
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void topUpBalanceNegativeTest() {
        Long id = 1L;
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(-1L));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/payments/1/receipt")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockReceiptDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                });
    }

    @Test
    @Order(5)
    void getBalanceIsUnauthorizedTest() {
        webTestClient.get()
                .uri("/api/payments/1/balance")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Order(6)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void getBalanceTest() {
        Long id = 1L;
        webTestClient.get()
                .uri("/api/payments/1/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.TEN);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                });
    }

    @Test
    @Order(7)
    void makePaymentIsUnauthorizedTest() {
        Long id = 1L;
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.TEN);

        webTestClient.post()
                .uri("/api/payments/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockPaymentDto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Order(8)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void makePaymentTest() {
        Long id = 1L;
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.TEN);

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/payments/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockPaymentDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                });
    }

    @Test
    @Order(9)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void makePaymentLowBalanceTest() {
        Long id = 1L;
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.TEN);

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/payments/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockPaymentDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                });
    }

    @Test
    @Order(10)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void makePaymentNullTest() {
        Long id = 1L;
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(null);

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/payments/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockPaymentDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                });
    }

    @Test
    @Order(11)
    @WithMockUser(roles = "PAYMENTS_SERVICE_CLIENT")
    void makePaymentNegativeTest() {
        Long id = 1L;
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(-1L));

        webTestClient.mutateWith(csrf())
                .post()
                .uri("/api/payments/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockPaymentDto)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.ZERO);
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(false);
                });
    }
}
