package ru.ugaforever.market.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.ugaforever.market.entity.Item;
import ru.ugaforever.market.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {
    private final ItemRepository itemRepository;

    @Override
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
    }
}
