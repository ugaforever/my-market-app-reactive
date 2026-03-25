package ru.ugaforever.reactive.market.payment.server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.server.domain.BalanceResponse;
import ru.ugaforever.reactive.market.payment.server.service.BalanceService;

@RestController
public class BalanceController implements BalanceApi {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public Mono<ResponseEntity<BalanceResponse>> getBalance(String accountId, ServerWebExchange exchange) {
        return balanceService.getBalance(accountId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}