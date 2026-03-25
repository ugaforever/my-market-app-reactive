package ru.ugaforever.reactive.market.payment.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.server.domain.PaymentRequest;
import ru.ugaforever.reactive.market.payment.server.domain.PaymentResponse;
import ru.ugaforever.reactive.market.payment.server.service.BalanceService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PaymentController implements PaymentsApi {

    private final BalanceService balanceService;

    public PaymentController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> processPayment(Mono<PaymentRequest> paymentRequest,
                                                                ServerWebExchange exchange) {

        return paymentRequest
                .flatMap(request -> balanceService.deduct(request.getAccountId(), request.getAmount())
                        .map(balance -> new PaymentResponse()
                                .transactionId(UUID.randomUUID().toString())
                                .newBalance(balance.getBalance())
                                .status("SUCCESS"))
                        .map(ResponseEntity::ok)
                        .onErrorResume(IllegalArgumentException.class, e ->
                                Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build()))
                );
    }
}
