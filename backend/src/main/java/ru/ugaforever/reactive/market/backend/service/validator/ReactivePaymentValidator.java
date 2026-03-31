package ru.ugaforever.reactive.market.backend.service.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.client.model.PaymentResponse;

@Component
public class ReactivePaymentValidator {
    public Mono<PaymentResponse> validate(PaymentResponse paymentResponse) {
        if (!"SUCCESS".equals(paymentResponse.getStatus())) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "Оплата не прошла: " + paymentResponse.getStatus()
            ));
        }
        return Mono.just(paymentResponse);
    }
}
