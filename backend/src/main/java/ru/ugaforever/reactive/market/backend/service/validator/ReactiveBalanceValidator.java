package ru.ugaforever.reactive.market.backend.service.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.client.model.BalanceResponse;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ReactiveBalanceValidator {
    public Mono<BalanceResponse> validate(BalanceResponse balance, Long totalSum) {
        if (Optional.ofNullable(balance.getBalance())
                .map(BigDecimal::longValue)
                .orElse(0L) < totalSum) {

            return Mono.error(new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "Недостаточно средств. Доступно: " + Optional.ofNullable(balance.getBalance()).orElse(BigDecimal.ZERO)
            ));
        }
        return Mono.just(balance);
    }
}
