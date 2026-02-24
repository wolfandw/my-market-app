package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.model.Item;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Интеграционные тесты сервиса товаров.
 */
public class ItemServiceTest extends AbstractServiceTest {
    @Test
    public void getItemsDefaultTest() {
        Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());
        List<Item> content = ITEMS.values().stream().limit(5).toList();
        Flux<Item> page = Flux.fromIterable(content);
        Mono<Long> count = Mono.just((long)ITEMS.size());

        when(itemsCache.getItems("", "NO", 1, 5)).thenReturn(Flux.empty());
        when(itemsCache.cache("", "NO", 1, 5, page)).thenReturn(page);
        when(itemRepository.findAllBy(pageable)).thenReturn(page);

        when(itemsCountCache.getItemsCount("", 1, 5)).thenReturn(Mono.empty());
        when(itemsCountCache.cache("", 1, 5, count)).thenReturn(count);
        when(itemRepository.count()).thenReturn(count);

        mockCartItem();

        StepVerifier.create(itemService.getItems(DEFAULT_CART_ID, null, "NO", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    Assertions.assertThat(itemsPage).size().isEqualTo(5);
                    Assertions.assertThat(itemsPage.get(4).title()).isEqualTo("Item 11");
                }).verifyComplete();

        StepVerifier.create(itemService.getItemsPaging(null, 1, 5)).
                consumeNextWith(actualPaging -> {
                    Assertions.assertThat(actualPaging.pageSize()).isEqualTo(5);
                    Assertions.assertThat(actualPaging.pageNumber()).isEqualTo(1);
                    Assertions.assertThat(actualPaging.hasPrevious()).isFalse();
                    Assertions.assertThat(actualPaging.hasNext()).isTrue();
                }).verifyComplete();
    }

    @Test
    void getItemTest() {
        Long itemId = 1L;
        Mono<Item> item = Mono.just( ITEMS.get(itemId));
        when(itemCache.getItem(itemId)).thenReturn(Mono.empty());
        when(itemCache.cache(item)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(item);

        mockCartItem();

        StepVerifier.create(itemService.getItem(DEFAULT_CART_ID, 1L)).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 07 SearchTag")).verifyComplete();
    }

    @Test
    void getItemEmptyTest() {
        Long itemId = 14L;
        Mono<Item> item = Mono.empty();
        when(itemCache.getItem(itemId)).thenReturn(item);
        when(itemCache.cache(item)).thenReturn(item);
        when(itemRepository.findById(itemId)).thenReturn(item);

        mockCartItem();

        StepVerifier.create(itemService.getItem(DEFAULT_CART_ID, itemId)).expectNextCount(0).verifyComplete();
    }

    @Test
    void createItem() {
        Long itemId = 14L;
        String title = "Item 14";
        String description = "Item 14 description";
        BigDecimal price = BigDecimal.valueOf(14);

        Mono<Item> item = Mono.just(new Item(itemId, title, description, null, price));
        when(itemCache.cache(item)).thenReturn(item);
        when(itemsCache.clear()).thenReturn(Mono.just((long)ITEMS.size()));
        when(itemsCountCache.clear()).thenReturn(Mono.just(1L));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);

        trxStepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14))).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 14")).verifyComplete();
    }
}
