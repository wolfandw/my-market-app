package io.github.wolfandw.payment.service.impl;

import io.github.wolfandw.payment.server.domain.BalanceDto;
import io.github.wolfandw.payment.service.PaymentsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Реализация {@link PaymentsService}
 */
@Service
public class PaymentsServiceImpl implements PaymentsService {
    private final Map<Long, BigDecimal> balances = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    @Override
    public synchronized Mono<BalanceDto> getBalance(Long id) {
        BalanceDto balanceDto = new BalanceDto();
        balanceDto.setBalance(balances.getOrDefault(id, BigDecimal.ZERO));
        balanceDto.setId(id);
        balanceDto.setAccept(true);
        return Mono.just(balanceDto);
    }

    @Override
    public Mono<BalanceDto> makePayment(Long id, BigDecimal payment) {
        synchronized (lock) {
            BigDecimal balance = balances.getOrDefault(id, BigDecimal.ZERO);
            BalanceDto balanceDto = new BalanceDto();
            balanceDto.setId(id);
            balanceDto.setAccept(true);
            if (payment == null || payment.compareTo(BigDecimal.ZERO) < 1 || balance.compareTo(payment) < 0) {
                balanceDto.setBalance(balance);
                balanceDto.setAccept(false);
            } else {
                balances.put(id, balance.subtract(payment));
                balanceDto.setBalance(balances.get(id));
            }
            return Mono.just(balanceDto);
        }
    }

    @Override
    public synchronized Mono<BalanceDto> topUpBalance(Long id, BigDecimal receipt) {
        synchronized (lock) {
            BigDecimal balance = balances.getOrDefault(id, BigDecimal.ZERO);
            BalanceDto balanceDto = new BalanceDto();
            balanceDto.setId(id);
            balanceDto.setAccept(true);
            if (receipt == null || receipt.compareTo(BigDecimal.ZERO) < 1) {
                balanceDto.setBalance(balance);
                balanceDto.setAccept(false);
            }
            else {
                balances.put(id, balance.add(receipt));
                balanceDto.setBalance(balances.get(id));
            }
            return Mono.just(balanceDto);
        }
    }
}
