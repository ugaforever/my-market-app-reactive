package ru.ugaforever.reactive.market.payment.server.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.server.config.SecurityConfig;
import ru.ugaforever.reactive.market.payment.server.domain.BalanceResponse;
import ru.ugaforever.reactive.market.payment.server.domain.PaymentRequest;
import ru.ugaforever.reactive.market.payment.server.service.BalanceService;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebFluxTest(
        controllers = PaymentController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveOAuth2ClientAutoConfiguration.class,
                ReactiveOAuth2ResourceServerAutoConfiguration.class
        }
)
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BalanceService balanceService;

    @Test
    void processPayment_ShouldReturnSuccess() {
        // Given
        String accountId = "12345";
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal newBalance = new BigDecimal("900.00");

        PaymentRequest request = new PaymentRequest();
        request.setAccountId(accountId);
        request.setAmount(amount);

        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setAccountId(accountId);
        balanceResponse.setBalance(newBalance);

        when(balanceService.deduct(accountId, amount))
                .thenReturn(Mono.just(balanceResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo("SUCCESS")
                .jsonPath("$.newBalance").isEqualTo(900.00)
                .jsonPath("$.transactionId").exists();
    }



}