package ru.ugaforever.reactive.market.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import ru.ugaforever.reactive.market.entity.Item;
import ru.ugaforever.reactive.market.repository.ItemRepository;


@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final ItemRepository itemRepository;

    @Override
    public void run(String... args) {
        Flux.range(1, 20)
                .map(i -> Item.builder()
                        .title("Товар " + i)
                        .price(i * 333.33)
                        .description("Описание " + i)
                        .imgPath(i + ".jpg")
                        .build()
                )
                .flatMap(itemRepository::save)
                .collectList()
                .subscribe();
    }



/*    @Override
    public void run(String... args) {
        List<Item> items = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            items.add(Item.builder()
                    .title("Товар № " + i)
                    .price((double)Math.round(i * 1000 / 3))
                    .description("Описание товара № " + i)
                    .imgPath(String.format("images/%d.jpg", i))
                    .build());
        }

        itemRepository.saveAll(items);
        System.out.println("Загружено товаров: " + itemRepository.count());
    }*/
}
