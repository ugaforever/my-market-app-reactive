package ru.ugaforever.reactive.market.payment.server.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('SERVICE')")
    public Mono<ResponseEntity<PaymentResponse>> processPayment(Mono<PaymentRequest> paymentRequest,
                                                                ServerWebExchange exchange) {

        return paymentRequest
                .flatMap(request -> balanceService.deduct(request.getAccountId(), request.getAmount())
                        .map(balance -> {
                            PaymentResponse response = new PaymentResponse()
                                    .transactionId(UUID.randomUUID().toString())
                                    .newBalance(balance.getBalance())
                                    .status("SUCCESS");

                                    return ResponseEntity.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(response);
                        }))
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(ResponseEntity
                                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(null)));
    }
}
