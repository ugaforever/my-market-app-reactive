package ru.ugaforever.reactive.market.payment.server.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.reactive.ReactiveOAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.server.domain.BalanceResponse;
import ru.ugaforever.reactive.market.payment.server.service.BalanceService;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebFluxTest(
        controllers = BalanceController.class,
        excludeAutoConfiguration = {
                ReactiveSecurityAutoConfiguration.class,
                ReactiveOAuth2ResourceServerAutoConfiguration.class
        }
)
class BalanceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BalanceService balanceService;

    @Test
    void getBalance_WhenAccountExists_ShouldReturnBalance() {

        BigDecimal accountId = new BigDecimal(12345);
        BalanceResponse expectedResponse = new BalanceResponse();
        expectedResponse.setBalance(accountId);
        expectedResponse.setBalance(new BigDecimal(1000.50));
        expectedResponse.setCurrency("RUB");

        when(balanceService.getBalance(accountId.toString()))
                .thenReturn(Mono.just(expectedResponse));

        webTestClient.get()
                .uri("/api/v1/balance/{accountId}", accountId.toString())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceResponse.class)
                .isEqualTo(expectedResponse);
    }
}