package ru.ugaforever.reactive.market.backend.config;

import io.netty.channel.ChannelOption;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.ugaforever.reactive.market.payment.client.ApiClient;
import ru.ugaforever.reactive.market.payment.client.api.BalanceApi;
import ru.ugaforever.reactive.market.payment.client.api.PaymentsApi;

import java.time.Duration;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "payment.service")
@Data
public class PaymentWebClientConfiguration {

    private String url = "http://localhost:9091/api/v1";
    private int connectTimeout = 5000;
    private int readTimeout = 5000;

    @Bean
    public ReactiveOAuth2AuthorizedClientManager paymentAuthorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrations,
            ServerOAuth2AuthorizedClientRepository authorizedClients) {

        // Только client_credentials flow
        var authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        // стандартный менеджер, который будет использовать переданные репозитории
        var authorizedClientManager = new DefaultReactiveOAuth2AuthorizedClientManager(
                // откуда брать настройки провайдера (client ID, secret, URL и т.д.)
                clientRegistrations,
                // куда кэшировать токены (в WebSession по умолчанию)
                authorizedClients);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    @Primary
    public WebClient paymentWebClient(ReactiveOAuth2AuthorizedClientManager paymentAuthorizedClientManager) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout));

        // OAuth2 фильтр для автоматической подстановки токена
        var oauth2Filter = new ServerOAuth2AuthorizedClientExchangeFilterFunction(paymentAuthorizedClientManager);
        oauth2Filter.setDefaultClientRegistrationId("payment-service");
        oauth2Filter.setDefaultOAuth2AuthorizedClient(true); // Всегда использовать client credentials

        return WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(oauth2Filter)
                .build();
    }

    @Bean
    public ApiClient paymentApiClient(WebClient paymentWebClient) {

        log.info("========================================");
        log.info("Creating ApiClient with URL: " + url);
        log.info("PAYMENT_SERVICE_URL from env: " + System.getenv("PAYMENT_SERVICE_URL"));
        log.info("========================================");

        ApiClient apiClient = new ApiClient(paymentWebClient);
        apiClient.setBasePath(url);
        return apiClient;
    }


    @Bean
    public PaymentsApi paymentsApi(ApiClient apiClient) {
        return new PaymentsApi(apiClient);
    }

    @Bean
    public BalanceApi balanceApi(ApiClient apiClient) {
        return new BalanceApi(apiClient);
    }

}
