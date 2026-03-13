package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.dto.UserInfoDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.mymarket.test.service.AbstractServiceTest;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Интеграционный тест сервиса платежей.
 */
public class PaymentsServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    @IsRoleUser
    public void getBalanceUserTest() {
        StepVerifier.create(paymentsService.getBalance(getUser().getId())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void getBalanceAdminTest() {
        getBalanceTest(paymentsService.getBalance(getAdmin().getId()), getAdminInfo());
    }

    @Test
    @IsRoleGuest
    public void getBalanceGuestTest() {
        StepVerifier.create(paymentsService.getBalance(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void getUserBalanceUserTest() {
        getBalanceTest(paymentsService.getUserBalance(), getUserInfo());
    }

    @Test
    @IsRoleGuest
    public void getUserBalanceGuestTest() {
        StepVerifier.create(paymentsService.getUserBalance()).verifyError(AuthorizationDeniedException.class);
    }

    private void getBalanceTest(Mono<BalanceDto> balance, UserInfoDto userInfo) {
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(userInfo.userId());
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(paymentsApi.getBalance(eq(userInfo.userId()))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(balance).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(userInfo.userId());
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    public void getBalanceErrorTest() {
        when(paymentsApi.getBalance(any(Long.class))).thenReturn(Mono.empty());
        StepVerifier.create(paymentsService.getBalance(getAdmin().getId())).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void makePaymentUserTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        StepVerifier.create(paymentsService.makePayment(getUser().getId(), mockPaymentDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void makePaymentAdminTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        makePaymentTest(paymentsService.makePayment(getAdmin().getId(), mockPaymentDto), getAdminInfo());
    }

    @Test
    @IsRoleGuest
    public void makePaymentGuestTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        StepVerifier.create(paymentsService.makePayment(ID_GUEST, mockPaymentDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void makeUserPaymentUserTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        makePaymentTest(paymentsService.makeUserPayment(mockPaymentDto), getUserInfo());
    }

    @Test
    @IsRoleGuest
    public void makeUserPaymentGuestTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        StepVerifier.create(paymentsService.makeUserPayment(mockPaymentDto)).verifyError(AuthorizationDeniedException.class);
    }

    private void makePaymentTest(Mono<BalanceDto> balance, UserInfoDto userInfo) {
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(userInfo.userId());
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));

        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(balance).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(userInfo.userId());
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    public void makePaymentBadBalanceTest() {
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(getAdmin().getId());
        mockBalanceDto.setAccept(false);

        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getAdmin().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(paymentsService.makePayment(getAdmin().getId(), mockPaymentDto)).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    public void makePaymentServiceErrorTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getAdmin().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(paymentsService.makePayment(getAdmin().getId(), mockPaymentDto)).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void topUpBalanceUserTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(getUser().getId());
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        StepVerifier.create(paymentsService.topUpBalance(getUser().getId(), mockReceiptDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void topUpBalanceAdminTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(getAdmin().getId());
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        topUpBalanceTest(paymentsService.topUpBalance(getAdmin().getId(), mockReceiptDto), getAdminInfo());
    }

    @Test
    @IsRoleGuest
    public void topUpBalanceGuestTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(ID_GUEST);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        StepVerifier.create(paymentsService.topUpBalance(ID_GUEST, mockReceiptDto)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void topUpUserBalanceUserTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(getUser().getId());
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        topUpBalanceTest(paymentsService.topUpUserBalance(mockReceiptDto), getUserInfo());
    }

    @Test
    @IsRoleGuest
    public void topUpUserBalanceGuestTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(ID_GUEST);
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        StepVerifier.create(paymentsService.topUpUserBalance(mockReceiptDto)).verifyError(AuthorizationDeniedException.class);
    }

    private void topUpBalanceTest(Mono<BalanceDto> balance, UserInfoDto userInfo) {
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(userInfo.userId());
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));

        when(paymentsApi.topUpBalance(any(Long.class), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));

        StepVerifier.create(balance).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(userInfo.userId());
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(BigDecimal.valueOf(8000L));
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(true);
                }).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    public void topUpBalanceServiceErrorTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(getAdmin().getId());
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(any(Long.class), any(ReceiptDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));

        StepVerifier.create(paymentsService.topUpBalance(getAdmin().getId(), mockReceiptDto)).verifyComplete();
    }
}
