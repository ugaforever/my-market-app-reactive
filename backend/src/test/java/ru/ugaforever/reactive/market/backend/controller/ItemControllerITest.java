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
class ItemControllerITest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetItems_ShouldReturnItems() {
        int pageSize = 5;

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam("pageSize", pageSize)
                        .queryParam("pageNumber", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class)
                .value(html -> {
                    assertThat(html).contains("Витрина магазина");
                    assertThat(html).contains("Корзина");
                    assertThat(html).contains("Следующая");
                });
    }
}

