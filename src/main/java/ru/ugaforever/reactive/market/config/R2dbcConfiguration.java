package ru.ugaforever.reactive.market.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import ru.ugaforever.reactive.market.callback.ValidateOrderIdBeforeSaveCallback;
import ru.ugaforever.reactive.market.converter.OrderMetadataReadConverter;
import ru.ugaforever.reactive.market.converter.OrderMetadataWriteConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    private final ConnectionFactory connectionFactory;

    public R2dbcConfiguration(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public ConnectionFactory connectionFactory() {
        return connectionFactory;
    }

    @Override
    protected List<Object> getCustomConverters() {
        List<Object> converters = new ArrayList<>();
        converters.add(new OrderMetadataReadConverter());
        converters.add(new OrderMetadataWriteConverter());
        return converters;
    }

    @Bean
    public ValidateOrderIdBeforeSaveCallback validateOrderIdBeforeConvertCallback() {
        return new ValidateOrderIdBeforeSaveCallback();
    }

}
