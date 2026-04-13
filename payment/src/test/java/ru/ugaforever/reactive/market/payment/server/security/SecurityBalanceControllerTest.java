package ru.ugaforever.reactive.market.payment.server.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.ugaforever.reactive.market.payment.server.api.BalanceController;
import ru.ugaforever.reactive.market.payment.server.domain.BalanceResponse;
import ru.ugaforever.reactive.market.payment.server.service.BalanceService;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@WebFluxTest(BalanceController.class)
public class SecurityBalanceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BalanceService balanceService;

    @Test
    void shouldReturn401_WhenNoTokenProvided() {
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
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(authorities = "SERVICE")
    void shouldReturnMessage_WhenHasServiceRole() {
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
                .expectStatus().isOk();
    }
}
