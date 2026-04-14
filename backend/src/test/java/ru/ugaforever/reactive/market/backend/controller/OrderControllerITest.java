package ru.ugaforever.reactive.market.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.ugaforever.reactive.market.backend.config.ReactiveDatabaseInitializer;
import ru.ugaforever.reactive.market.backend.config.TestSecurityConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({ReactiveDatabaseInitializer.class, TestSecurityConfiguration.class})
@AutoConfigureWebTestClient
class OrderControllerITest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetOrders_ShouldReturnEmptyList_WhenNoOrders() {
        // Act & Assert
        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(result -> {
                    String responseBody = new String(result.getResponseBody());
                    assertThat(responseBody).contains("Нет заказов");
                });
    }
}
