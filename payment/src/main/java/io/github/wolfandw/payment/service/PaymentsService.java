package io.github.wolfandw.payment.service;

import io.github.wolfandw.payment.server.domain.BalanceDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Сервис платежей.
 */
public interface PaymentsService {
    /**
     * Подучить баланс клиента.
     *
     * @param id идентификатор клиента
     * @return баланс клиента
     */
   Mono<BalanceDto> getBalance(Long id);

    /**
     * Выполнить платеж клиента.
     *
     * @param id идентификатор клиента
     * @param payment сумма платежа
     * @return новый баланс, после платежа
     */
   Mono<BalanceDto> makePayment(Long id, BigDecimal payment);

    /**
     * Пополнить баланс клиента.
     *
     * @param id идентификатор клиента
     * @param receipt сумма пополнения
     * @return новый баланс, после пополнения
     */
   Mono<BalanceDto> topUpBalance(Long id,  BigDecimal receipt);
}
