package ru.ugaforever.reactive.market.payment.server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.server.domain.BalanceResponse;
import ru.ugaforever.reactive.market.payment.server.service.BalanceService;

@RestController
@RequestMapping("/api/v1")
public class BalanceController implements BalanceApi {

    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    //@PreAuthorize("hasAuthority('SERVICE')")
    public Mono<ResponseEntity<BalanceResponse>> getBalance(
            @PathVariable("accountId") String accountId,
            ServerWebExchange exchange) {
        return balanceService.getBalance(accountId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}