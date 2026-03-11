package io.github.wolfandw.mymarket.test.service;

import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Интеграционные тесты сервиса товаров.
 */
public class ItemServiceTest extends AbstractServiceTest {
    @MockitoBean(reset = MockReset.BEFORE)
    private UserService mockUserService;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void getItemsDefaultTest(boolean emptyCache) {
        Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());
        List<Item> content = ITEMS.values().stream().limit(5).toList();
        Flux<Item> page = Flux.fromIterable(content);
        Mono<Long> count = Mono.just((long)ITEMS.size());

        when(itemsCache.getItems("", "NO", 1, 5)).thenReturn(emptyCache ? Flux.empty() : page);
        when(itemsCountCache.getItemsCount("", 1, 5)).thenReturn(emptyCache ? Mono.empty() : count);

        when(itemsCache.cache("", "NO", 1, 5, page)).thenReturn(page);
        when(itemsCountCache.cache("", 1, 5, count)).thenReturn(count);

        when(itemRepository.findAllBy(pageable)).thenReturn(page);
        when(itemRepository.count()).thenReturn(count);
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(1L));

        mockUserCart();
        mockCartItem();

        StepVerifier.create(itemService.getItems(null, "NO", 1, 5).collectList()).
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

        verify(itemsCache, times(emptyCache ? 1 : 0)).cache("", "NO", 1, 5, page);
        verify(itemsCountCache, times(emptyCache ? 1 : 0)).cache("", 1, 5, count);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getItemTest(boolean emptyCache) {
        Long itemId = 1L;
        Mono<Item> item = Mono.just( ITEMS.get(itemId));

        when(itemCache.getItem(itemId)).thenReturn(emptyCache ? Mono.empty() : item);
        when(itemCache.cache(item)).thenReturn(item);
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(1L));

        when(itemRepository.findById(itemId)).thenReturn(item);

        mockUserCart();
        mockCartItem();

        StepVerifier.create(itemService.getItem(1L)).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 07 SearchTag")).verifyComplete();

        verify(itemCache, times(emptyCache ? 1 : 0)).cache(item);
    }

    @Test
    void getItemEmptyTest() {
        Long itemId = 14L;
        Mono<Item> item = Mono.empty();

        when(itemCache.getItem(itemId)).thenReturn(item);
        when(itemCache.cache(item)).thenReturn(item);

        when(itemRepository.findById(itemId)).thenReturn(item);
        when(mockUserService.getCurrentUserId()).thenReturn(Mono.just(1L));

        mockCartItem();

        StepVerifier.create(itemService.getItem(itemId)).expectNextCount(0).verifyComplete();
    }

    private void prepareCreateItemTest() {
        Long itemId = 14L;
        String title = "Item 14";
        String description = "Item 14 description";
        BigDecimal price = BigDecimal.valueOf(14);

        Mono<Item> item = Mono.just(new Item(itemId, title, description, null, price));
        when(itemCache.cache(item)).thenReturn(item);
        when(itemsCache.clear()).thenReturn(Mono.just((long)ITEMS.size()));
        when(itemsCountCache.clear()).thenReturn(Mono.just(1L));
        when(itemRepository.save(Mockito.any(Item.class))).thenReturn(item);
    }

    @Test
    void createItemIsUnauthorizedTest() {
        prepareCreateItemTest();
        trxStepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14))).
                verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void createItemIsUserTest() {
        prepareCreateItemTest();
        trxStepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14))).
                verifyError(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createItemIsAdminTest() {
        prepareCreateItemTest();
        trxStepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14))).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 14")).verifyComplete();
    }
}
