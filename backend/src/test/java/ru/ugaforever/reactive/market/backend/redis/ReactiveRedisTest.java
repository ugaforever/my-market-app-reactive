package ru.ugaforever.reactive.market.backend.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.ugaforever.reactive.market.backend.config.EmbeddedRedisConfiguration;
import ru.ugaforever.reactive.market.backend.dto.ItemDTO;
import ru.ugaforever.reactive.market.backend.mapper.ItemMapper;
import ru.ugaforever.reactive.market.backend.model.Item;
import ru.ugaforever.reactive.market.backend.repository.ReactiveItemRepository;
import ru.ugaforever.reactive.market.backend.service.ReactiveItemCacheService;
import ru.ugaforever.reactive.market.backend.service.ReactiveItemService;

import static org.mockito.Mockito.*;


@SpringBootTest
@Import(EmbeddedRedisConfiguration.class)
@AutoConfigureWebTestClient
@EnableCaching
public class ReactiveRedisTest {

    @Autowired
    private ReactiveItemCacheService itemService;

    @Autowired
    private ItemMapper itemMapper;

    @MockitoBean
    private ReactiveItemRepository itemRepository;

    @Test
    void testFindById_CachesResult_WhenCalledFirstTime() {

        Long itemId = 1L;
        Item expectedItem = Item.builder()
                .id(itemId)
                .title("Test Item")
                .description("Description Item")
                .price(100.0)
                .build();

        when(itemRepository.findById(itemId))
                .thenReturn(Mono.just(expectedItem));


        Mono<ItemDTO> firstCall = itemService.findById(itemId);

        StepVerifier.create(firstCall)
                .expectNext(itemMapper.toDto(expectedItem))
                .verifyComplete();

        verify(itemRepository, times(1)).findById(itemId);

        Mono<ItemDTO> secondCall = itemService.findById(itemId);

        StepVerifier.create(secondCall)
                .expectNext(itemMapper.toDto(expectedItem))
                .verifyComplete();

        verify(itemRepository, times(1)).findById(itemId);
    }
}
