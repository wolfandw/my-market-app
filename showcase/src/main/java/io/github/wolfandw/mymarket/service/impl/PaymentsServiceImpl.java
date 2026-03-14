package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.mymarket.service.UserService;
import io.github.wolfandw.payment.client.api.PaymentsApi;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Реализация {@link PaymentsService}
 */
@Service
public class PaymentsServiceImpl implements PaymentsService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentsServiceImpl.class);
    public static final String PAYMENTS_API_UNAVAILABLE = "Сервис платежей недоступен: {}";

    private final PaymentsApi paymentsApi;
    private final UserService userService;

    /**
     * Создает обертку Api сервиса платежей.
     *
     * @param paymentsApi    Api сервиса платежей
     * @param userService        сервис пользователей
     */
    public PaymentsServiceImpl(PaymentsApi paymentsApi, UserService userService) {
        this.paymentsApi = paymentsApi;
        this.userService = userService;
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<BalanceDto> getBalance(Long userId) {
        return getBalance(Mono.just(userId));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<BalanceDto> makePayment(Long userId, PaymentDto paymentDto) {
        return makePayment(Mono.just(userId), paymentDto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<BalanceDto> topUpBalance(Long userId, ReceiptDto receiptDto) {
        return topUpBalance(Mono.just(userId), receiptDto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Mono<BalanceDto> getUserBalance() {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        return getBalance(userIdMono);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Mono<BalanceDto> makeUserPayment(PaymentDto paymentDto) {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        return  makePayment(userIdMono, paymentDto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public Mono<BalanceDto> topUpUserBalance(ReceiptDto receiptDto) {
        Mono<Long> userIdMono = userService.getCurrentUserId();
        return topUpBalance(userIdMono, receiptDto);
    }

    private Mono<BalanceDto> getBalance(Mono<Long> userIdMono) {
        return userIdMono.flatMap(paymentsApi::getBalance).onErrorResume(onPaymentsApiError());
    }

    private Mono<BalanceDto> makePayment(Mono<Long> userIdMono, PaymentDto paymentDto) {
        return userIdMono.flatMap(userId -> paymentsApi.makePayment(userId, paymentDto)).
                filter(balanceDto -> Boolean.TRUE.equals(balanceDto.getAccept())).
                onErrorResume(onPaymentsApiError());
    }

    private Mono<BalanceDto> topUpBalance(Mono<Long> userIdMono, ReceiptDto receiptDto) {
        return userIdMono.flatMap(userId ->paymentsApi.topUpBalance(userId, receiptDto)).onErrorResume(onPaymentsApiError());
    }

    private static @NonNull Function<Throwable, Mono<? extends BalanceDto>> onPaymentsApiError() {
        return e -> {
            LOG.error(PAYMENTS_API_UNAVAILABLE, e.getMessage(), e);
            return Mono.empty();
        };
    }
}
