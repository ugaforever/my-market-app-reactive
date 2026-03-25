package ru.ugaforever.reactive.market.payment.server.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.server.domain.BalanceResponse;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BalanceService {
    private final Map<String, BigDecimal> balances = new ConcurrentHashMap<>();

    public BalanceService() {
        balances.put("user123", new BigDecimal(1000.0));
        balances.put("user456", new BigDecimal(50.0));
    }

    public Mono<BalanceResponse> getBalance(String accountId) {
        BigDecimal balance = balances.get(accountId);
        if (balance == null) {
            return Mono.empty();
        }
        return Mono.just(new BalanceResponse()
                .accountId(accountId)
                .balance(balance)
                .currency("RUB"));
    }

    public Mono<BalanceResponse> deduct(String accountId, BigDecimal amount) {
        BigDecimal balance = balances.get(accountId);
        if (balance == null) {
            return Mono.empty();
        }
        if (balance.compareTo(amount) < 0) {
            return Mono.error(new IllegalArgumentException("Insufficient funds"));
        }
        BigDecimal newBalance = balance.subtract(amount);
        balances.put(accountId, newBalance);
        return Mono.just(new BalanceResponse()
                .accountId(accountId)
                .balance(newBalance)
                .currency("RUB"));
    }
}