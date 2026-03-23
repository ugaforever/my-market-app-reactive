package ru.ugaforever.reactive.market.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.ugaforever.reactive.market.backend.model.Item;
import ru.ugaforever.reactive.market.backend.repository.ReactiveItemRepository;


@Component
@RequiredArgsConstructor
public class ReactiveDatabaseInitializer implements CommandLineRunner {

    private final ReactiveItemRepository itemRepository;

    @Override
    public void run(String... args) {
        Flux.range(1, 20)
                .map(i -> Item.builder()
                        .title("Товар № " + i)
                        .price((double)Math.round(i * 1000 / 3))
                        .description("Описание товара № " + i)
                        .imgPath(String.format("images/%d.jpg", i))
                        .build()
                )
                .flatMap(itemRepository::save)
                .collectList()
                .subscribe();
    }
}
