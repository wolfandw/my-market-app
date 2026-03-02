package io.github.wolfandw.payment.test.controller;

import io.github.wolfandw.payment.controller.PaymentsController;
import io.github.wolfandw.payment.server.domain.BalanceDto;
import io.github.wolfandw.payment.server.domain.PaymentDto;
import io.github.wolfandw.payment.server.domain.ReceiptDto;
import io.github.wolfandw.payment.service.PaymentsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Модульные тесты контроллера платежей.
 */
@WebFluxTest(PaymentsController.class)
public class PaymentsControllerTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private PaymentsService paymentsService;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void getBalanceTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(paymentsService.getBalance(any(Long.class))).thenReturn(Mono.just(mockBalanceDto));


        webTestClient.get()
                .uri("/api/payments/1/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceDto.class)
                .value(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                });
    }

    @Test
    void makePaymentTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.TEN);
        when(paymentsService.makePayment(any(Long.class), any(BigDecimal.class))).thenReturn(Mono.just(mockBalanceDto));

        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.TEN);

        webTestClient.post()
                .uri("/api/payments/1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mockPaymentDto)
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
    void topUpBalance() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.TEN);
        when(paymentsService.topUpBalance(any(Long.class), any(BigDecimal.class))).thenReturn(Mono.just(mockBalanceDto));

        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.TEN);

        webTestClient.post()
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
}
