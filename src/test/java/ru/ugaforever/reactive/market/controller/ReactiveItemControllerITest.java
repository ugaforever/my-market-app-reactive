package ru.ugaforever.reactive.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.springframework.web.reactive.result.view.Rendering;
import ru.ugaforever.reactive.market.config.ReactiveDatabaseInitializer;
import ru.ugaforever.reactive.market.dto.ItemDTO;
import ru.ugaforever.reactive.market.service.ReactiveItemService;


import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Import(ReactiveDatabaseInitializer.class)
@AutoConfigureWebTestClient
class ReactiveItemControllerITest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testGetItems_ShouldReturnItems() throws Exception {
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

