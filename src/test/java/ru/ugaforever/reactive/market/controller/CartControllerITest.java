package ru.ugaforever.reactive.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.ugaforever.reactive.market.config.ReactiveDatabaseInitializer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(ReactiveDatabaseInitializer.class)
@AutoConfigureWebTestClient
class CartControllerITest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetItemsCart_ShouldReturnEmptyCart_WhenNoItems() {
        // Act & Assert
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(result -> {
                    String responseBody = new String(result.getResponseBody());
                    assertThat(responseBody).contains("Корзина пуста");
                });
    }
}

