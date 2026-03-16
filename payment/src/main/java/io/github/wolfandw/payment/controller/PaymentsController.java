package io.github.wolfandw.payment.controller;

import io.github.wolfandw.payment.server.api.PaymentsApi;
import io.github.wolfandw.payment.server.domain.BalanceDto;
import io.github.wolfandw.payment.server.domain.PaymentDto;
import io.github.wolfandw.payment.server.domain.ReceiptDto;
import io.github.wolfandw.payment.service.PaymentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Контроллер сервиса платежей.
 */
@RestController
public class PaymentsController implements PaymentsApi {
    private final PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }
    @Override
    @PreAuthorize("hasRole('PAYMENTS_SERVICE_CLIENT')")
    public Mono<ResponseEntity<BalanceDto>> getBalance(Long id, ServerWebExchange exchange) {
        return paymentsService
                .getBalance(id)
                .map(balanceDto -> ResponseEntity.status(HttpStatus.OK).body(balanceDto));
    }

    @Override
    @PreAuthorize("hasRole('PAYMENTS_SERVICE_CLIENT')")
    public Mono<ResponseEntity<BalanceDto>> makePayment(Long id, Mono<PaymentDto> paymentRequest, ServerWebExchange exchange) {
        return paymentRequest.flatMap(request -> paymentsService
                .makePayment(id, request.getPayment())
                .map(balanceDto -> ResponseEntity.
                        status(HttpStatus.ACCEPTED).body(balanceDto)));
    }

    @Override
    @PreAuthorize("hasRole('PAYMENTS_SERVICE_CLIENT')")
    public Mono<ResponseEntity<BalanceDto>> topUpBalance(Long id, Mono<ReceiptDto> receiptRequest, ServerWebExchange exchange) {
        return  receiptRequest.flatMap(request -> paymentsService
                .topUpBalance(id, request.getReceipt())
                .map(balanceDto -> ResponseEntity.status(HttpStatus.ACCEPTED).body(balanceDto)));
    }
}
