package ru.ugaforever.reactive.market.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.backend.exception.PaymentServiceException;
import ru.ugaforever.reactive.market.payment.client.api.BalanceApi;
import ru.ugaforever.reactive.market.payment.client.api.PaymentsApi;
import ru.ugaforever.reactive.market.payment.client.model.BalanceResponse;
import ru.ugaforever.reactive.market.payment.client.model.PaymentRequest;
import ru.ugaforever.reactive.market.payment.client.model.PaymentResponse;

@Service
@RequiredArgsConstructor
public class ReactivePaymentService {

    private final PaymentsApi paymentsApi;
    private final BalanceApi balanceApi;

    public Mono<PaymentResponse> processPayment(PaymentRequest request) {

        return paymentsApi.processPayment(request)
                .onErrorMap(WebClientResponseException.class, ex ->
                        ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY
                                ? new PaymentServiceException("Insufficient funds", ex)
                                : new PaymentServiceException("Payment processing failed", ex)
                );
    }

    public Mono<BalanceResponse> getBalance(String accountId){

        return balanceApi.getBalance(accountId)
                .onErrorMap(WebClientResponseException.class, ex ->
                        ex.getStatusCode() == HttpStatus.NOT_FOUND
                                ? new PaymentServiceException("Account not found: " + accountId, ex)
                                : new PaymentServiceException("Failed to get balance", ex)
                );
    }
}


