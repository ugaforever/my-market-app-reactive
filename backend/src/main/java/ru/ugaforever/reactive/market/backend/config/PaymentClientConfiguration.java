package ru.ugaforever.reactive.market.backend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.Data;
import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import ru.ugaforever.reactive.market.payment.client.ApiClient;
import ru.ugaforever.reactive.market.payment.client.api.BalanceApi;
import ru.ugaforever.reactive.market.payment.client.api.PaymentsApi;

import java.time.Duration;

@Configuration
public class PaymentClientConfiguration {

    private String url = "http://localhost:9091";
    private int connectTimeout = 5000;
    private int readTimeout = 5000;

    @Bean
    public ApiClient paymentApiClient() {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout));

        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        return new ApiClient(webClient);
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
