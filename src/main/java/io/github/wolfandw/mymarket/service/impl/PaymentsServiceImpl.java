package io.github.wolfandw.mymarket.service.impl;

import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.payment.client.api.PaymentsApi;
import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Реализация {@link PaymentsService}
 */
@Service
public class PaymentsServiceImpl implements PaymentsService {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentsServiceImpl.class);
    public static final String PAYMENTS_API_UNAVAILABLE = "Сервис платежей недоступен: {}";

    private final PaymentsApi paymentsApi;

    /**
     * Создает обертку Api сервиса платежей.
     *
     * @param paymentsApi    Api сервиса платежей
     */
    public PaymentsServiceImpl(PaymentsApi paymentsApi) {
        this.paymentsApi = paymentsApi;
    }

    @Override
    @Transactional
    public Mono<BalanceDto> getBalance(Long id) {
        return paymentsApi.getBalance(id).
                onErrorResume(e -> {
                    LOG.error(PAYMENTS_API_UNAVAILABLE, e.getMessage(), e);
                    return Mono.empty();
                });
    }

    @Override
    public Mono<BalanceDto> makePayment(Long id, PaymentDto paymentDto) {
        return paymentsApi.makePayment(id, paymentDto).
                filter(balanceDto -> Boolean.TRUE.equals(balanceDto.getAccept())).
                onErrorResume(e -> {
                    LOG.error(PAYMENTS_API_UNAVAILABLE, e.getMessage(), e);
                    return Mono.empty();
                });
    }

    @Override
    @Transactional
    public Mono<BalanceDto> topUpBalance(Long id, ReceiptDto receiptDto) {
        return paymentsApi.topUpBalance(id, receiptDto).
                onErrorResume(e -> {
                    LOG.error(PAYMENTS_API_UNAVAILABLE, e.getMessage(), e);
                    return Mono.empty();
                });
    }
}
