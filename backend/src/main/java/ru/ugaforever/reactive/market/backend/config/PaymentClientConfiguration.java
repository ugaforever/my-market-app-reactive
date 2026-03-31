package ru.ugaforever.reactive.market.backend.config;

import io.netty.channel.ChannelOption;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "payment.service")
@Data
public class PaymentClientConfiguration {

    private String url = "http://localhost:9091";
    private int connectTimeout = 5000;
    private int readTimeout = 5000;

    @Bean
    public ApiClient paymentApiClient() {

        log.info("========================================");
        log.info("Creating ApiClient with URL: " + url);
        log.info("PAYMENT_SERVICE_URL from env: " + System.getenv("PAYMENT_SERVICE_URL"));
        log.info("========================================");

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .responseTimeout(Duration.ofMillis(readTimeout));

        WebClient webClient = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        ApiClient apiClient = new ApiClient(webClient);
        // ЯВНО устанавливаем basePath без этого НЕ работает!!
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
