package ru.ugaforever.reactive.market.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import ru.ugaforever.reactive.market.backend.config.PaymentClientConfiguration;

@SpringBootApplication
@EnableCaching
public class MyMarketAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(MyMarketAppApplication.class, args);
	}

}
