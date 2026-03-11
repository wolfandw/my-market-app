package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.payment.client.domain.BalanceDto;
import io.github.wolfandw.payment.client.domain.PaymentDto;
import io.github.wolfandw.payment.client.domain.ReceiptDto;
import reactor.core.publisher.Mono;

/**
 * Обертка над сервисом платежей.
 */
public interface PaymentsService {
    /**
     * Получает баланс.
     *
     * @param id идентификатор клиента
     * @return баланс
     */
    Mono<BalanceDto> getBalance(Long id);

    /**
     * Выполняет платеж.
     *
     * @param id идентификатор клиента
     * @param paymentDto параметры платежа
     * @return новый баланс
     */
    Mono<BalanceDto> makePayment(Long id, PaymentDto paymentDto);

    /**
     * Пополняет баланс.
     *
     * @param receiptDto параметры пополнения
     * @return новый баланс
     */
    Mono<BalanceDto> topUpBalance(Long id, ReceiptDto receiptDto);

    /**
     * Получает баланс пользователя.
     *
     * @return баланс
     */
    Mono<BalanceDto> getUserBalance();

    /**
     * Выполняет платеж пользователя.
     *
     * @param paymentDto параметры платежа
     * @return новый баланс
     */
    Mono<BalanceDto> makeUserPayment(PaymentDto paymentDto);

    /**
     * Пополняет баланс пользователя.
     *
     * @param receiptDto параметры пополнения
     * @return новый баланс
     */
    Mono<BalanceDto> topUpUserBalance(ReceiptDto receiptDto);
}
