package ru.ugaforever.reactive.market.backend.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import ru.ugaforever.reactive.market.backend.model.OrderMetadata;

@ReadingConverter
public class OrderMetadataReadConverter implements Converter<String, OrderMetadata> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public OrderMetadata convert(String source) {
        try {
            return objectMapper.readValue(source, OrderMetadata.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка преобразования JSON в OrderMetadata", e);
        }
    }
}
