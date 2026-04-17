package ru.ugaforever.reactive.market.backend.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SecurityITest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private WebClient webClient;

    @Test
    void testGetAdmin_ShouldReturn200() {
        webTestClient.mutateWith(
                SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "admin", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                )
                        ))
                .get()
                .uri("/admin")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetAdmin_ShouldReturn403() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "admin", null, List.of(new SimpleGrantedAuthority("ROLE_NOT_ADMIN"))
                                )
                        ))
                .get()
                .uri("/admin")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void testGetHome_ShouldReturn200() {
        webTestClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testGetItems_ShouldRedirectToLogin_WhenUnauthenticated() {
        webTestClient
                .get().uri("/items")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", location ->
                        assertThat(location).contains("/oauth2/authorization/keycloak"));
    }
}