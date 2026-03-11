package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.mymarket.service.UserService;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
    @MockitoBean(reset = MockReset.BEFORE)
    private UserService mockUserService;

    private @NonNull Long prepareBalanceTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(paymentsApi.getBalance(eq(id))).thenReturn(Mono.just(mockBalanceDto));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(id));
        return id;
    }

    @Test
    void getBalanceIsUnauthorizedTest() {
        Long id = prepareBalanceTest();
        StepVerifier.create(paymentsService.getBalance(id)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void getUserBalanceIsUnauthorizedTest() {
        Long id = prepareBalanceTest();
        StepVerifier.create(paymentsService.getUserBalance()).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getBalanceIsUserTest() {
        Long id = prepareBalanceTest();
        StepVerifier.create(paymentsService.getBalance(id)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserBalanceIsUserTest() {
        Long id = prepareBalanceTest();
        StepVerifier.create(paymentsService.getUserBalance()).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBalanceIsAdminTest() {
        Long id = prepareBalanceTest();
        StepVerifier.create(paymentsService.getBalance(id)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getBalanceErrorIsAdminTest() {
        Long id = 1L;
        when(paymentsApi.getBalance(eq(id))).thenReturn(Mono.empty());
        StepVerifier.create(paymentsService.getBalance(id)).verifyComplete();
    }

    @Test
    void makePaymentIsUnauthorizedTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.makePayment(id, mockPaymentDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void makeUserPaymentIsUnauthorizedTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.makeUserPayment(mockPaymentDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void makePaymentIsUserTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.makePayment(id, mockPaymentDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void makeUserPaymentIsUserTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(id));

        StepVerifier.create(paymentsService.makeUserPayment(mockPaymentDto)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void makePaymentIsAdminTest() {
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
    @WithMockUser(roles = "ADMIN")
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
    @WithMockUser(roles = "ADMIN")
    void makePaymentServiceErrorTest() {
        Long id = 1L;
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(id);
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(id), any(PaymentDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(paymentsService.makePayment(id, mockPaymentDto)).verifyComplete();
    }

    @Test
    void topUpBalanceIsUnauthorizedTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(id), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.topUpBalance(id, mockReceiptDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void topUpUserBalanceIsUnauthorizedTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(id), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.topUpBalance(id, mockReceiptDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void topUpBalanceIsUserTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(id), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.topUpBalance(id, mockReceiptDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void topUpUserBalanceIsUserTest() {
        Long id = 1L;
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(id);
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(id), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(id));

        StepVerifier.create(paymentsService.topUpUserBalance(mockReceiptDto)).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(id);
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void topUpBalanceIsAdminTest() {
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
    @WithMockUser(roles = "ADMIN")
    void topUpBalanceServiceErrorTest() {
        Long id = 1L;
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(id);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(id), any(ReceiptDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(paymentsService.topUpBalance(id, mockReceiptDto)).verifyComplete();
    }
}
