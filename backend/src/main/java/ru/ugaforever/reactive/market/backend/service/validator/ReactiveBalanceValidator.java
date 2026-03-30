package ru.ugaforever.reactive.market.backend.service.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.client.model.BalanceResponse;

@Component
public class ReactiveBalanceValidator {
    public Mono<BalanceResponse> validate(BalanceResponse balance, Long totalSum) {
        if (balance.getBalance().longValue() < totalSum) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "Недостаточно средств. Доступно: " + balance.getBalance()
            ));
        }
        return Mono.just(balance);
    }

}
