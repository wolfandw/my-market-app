package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.model.User;
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
 * Модульный тест сервиса платежей.
 */
public class PaymentsServiceTest extends AbstractServiceTest {
    @Test
    @IsRoleUser
    public void getBalanceUserTest() {
        StepVerifier.create(paymentsService.getBalance(getUser().getId())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void getBalanceAdminTest() {
        balanceTest(getAdmin(), getAdminMono(), paymentsService.getBalance(getAdmin().getId()));
    }

    @Test
    @IsRoleGuest
    public void getBalanceGuestTest() {
        StepVerifier.create(paymentsService.getBalance(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void getUserBalanceUserTest() {
        balanceTest(getUser(), getUserMono(), paymentsService.getUserBalance());
    }

    @Test
    @IsRoleGuest
    public void getUserBalanceGuestTest() {
        StepVerifier.create(paymentsService.getBalance(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    void getBalanceErrorUserTest() {
        StepVerifier.create(paymentsService.getBalance(getUser().getId())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    void getBalanceErrorAdminTest() {
        when(paymentsApi.getBalance(any(Long.class))).thenReturn(Mono.empty());
        when(userRepository.findByUsername(any(String.class))).thenReturn(getAdminMono());
        StepVerifier.create(paymentsService.getBalance(getAdmin().getId())).verifyComplete();
    }

    @Test
    @IsRoleGuest
    void getBalanceErrorGuestTest() {
        StepVerifier.create(paymentsService.getBalance(ID_GUEST)).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void makePaymentUserTest() {
        StepVerifier.create(paymentsService.makePayment(getUser().getId(),  new PaymentDto())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void makePaymentAdminTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getAdmin().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        balanceTest(getAdmin(), getAdminMono(), paymentsService.makePayment(getAdmin().getId(), mockPaymentDto));
    }

    @Test
    @IsRoleGuest
    public void makePaymentGuestTest() {
        StepVerifier.create(paymentsService.makePayment(ID_GUEST,  new PaymentDto())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void makeUserPaymentUserTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        balanceTest(getUser(), getUserMono(), paymentsService.makeUserPayment(mockPaymentDto));
    }

    @Test
    @IsRoleGuest
    public void makeUserPaymentGuestTest() {
        StepVerifier.create(paymentsService.makeUserPayment(new PaymentDto())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void makeUserPaymentBadBalanceTest() {
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(getUser().getId());
        mockBalanceDto.setAccept(false);

        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(getUser().getId()), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));
        when(userRepository.findByUsername(any(String.class))).thenReturn(getUserMono());

        StepVerifier.create(paymentsService.makeUserPayment(mockPaymentDto)).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void makeUserPaymentServiceErrorTest() {
        PaymentDto mockPaymentDto = new PaymentDto();
        mockPaymentDto.setId(getUser().getId());
        mockPaymentDto.setPayment(BigDecimal.valueOf(1000L));
        when(paymentsApi.makePayment(eq(getUser().getId()), any(PaymentDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));
        when(userRepository.findByUsername(any(String.class))).thenReturn(getUserMono());

        StepVerifier.create(paymentsService.makeUserPayment(mockPaymentDto)).verifyComplete();
    }

    @Test
    @IsRoleUser
    public void topUpBalanceUserTest() {
        StepVerifier.create(paymentsService.topUpBalance(getUser().getId(),  new ReceiptDto())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleAdmin
    public void topUpBalanceAdminTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(getUser().getId());
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        balanceTest(getAdmin(), getAdminMono(), paymentsService.topUpBalance(getAdmin().getId(), mockReceiptDto));
    }

    @Test
    @IsRoleGuest
    public void topUpBalanceGuestTest() {
        StepVerifier.create(paymentsService.topUpBalance(ID_GUEST,  new ReceiptDto())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void topUpUserBalanceUserTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(getUser().getId());
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        balanceTest(getUser(), getUserMono(), paymentsService.topUpUserBalance(mockReceiptDto));
    }

    @Test
    @IsRoleGuest
    public void topUpUserBalanceGuestTest() {
        StepVerifier.create(paymentsService.topUpUserBalance(new ReceiptDto())).verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @IsRoleUser
    public void topUpUserBalanceServiceErrorTest() {
        ReceiptDto mockReceiptDto = new ReceiptDto();
        mockReceiptDto.setId(getUser().getId());
        mockReceiptDto.setReceipt(BigDecimal.valueOf(1000L));
        when(paymentsApi.topUpBalance(eq(getUser().getId()), any(ReceiptDto.class))).thenReturn(Mono.error(new IllegalArgumentException()));
        when(userRepository.findByUsername(any(String.class))).thenReturn(getUserMono());

        StepVerifier.create(paymentsService.topUpUserBalance(mockReceiptDto)).verifyComplete();
    }

    private void balanceTest(User testUser, Mono<User> testUserMono, Mono<BalanceDto> testBalanceMono) {
        BalanceDto mockBalanceDto = new BalanceDto();
        mockBalanceDto.setId(testUser.getId());
        mockBalanceDto.setAccept(true);
        mockBalanceDto.setBalance(BigDecimal.valueOf(8000L));
        when(paymentsApi.getBalance(any(Long.class))).thenReturn(Mono.just(mockBalanceDto));
        when(paymentsApi.makePayment(any(Long.class), any(PaymentDto.class))).thenReturn(Mono.just(mockBalanceDto));
        when(paymentsApi.topUpBalance(any(Long.class), any(ReceiptDto.class))).thenReturn(Mono.just(mockBalanceDto));
        when(userRepository.findByUsername(any(String.class))).thenReturn(testUserMono);

        StepVerifier.create(testBalanceMono).
                consumeNextWith(actualBalanceDto -> {
                    assertThat(actualBalanceDto.getId()).isEqualTo(mockBalanceDto.getId());
                    assertThat(actualBalanceDto.getBalance()).isEqualTo(mockBalanceDto.getBalance());
                    assertThat(actualBalanceDto.getAccept()).isEqualTo(mockBalanceDto.getAccept());
                }).verifyComplete();
    }
}
