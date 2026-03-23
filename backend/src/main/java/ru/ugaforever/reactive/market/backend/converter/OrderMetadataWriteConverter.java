package ru.ugaforever.reactive.market.backend.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import ru.ugaforever.reactive.market.backend.model.OrderMetadata;

@WritingConverter
public class OrderMetadataWriteConverter implements Converter<OrderMetadata, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convert(OrderMetadata source) {
        try {
            return objectMapper.writeValueAsString(source);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка преобразования OrderMetadata в JSON", e);
        }
    }
}
