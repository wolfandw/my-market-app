package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест сервиса товаров.
 */
public class ItemServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void getItemsTest() {
        StepVerifier.create(itemService.getItems(DEFAULT_CART_ID, null, "NO", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(5);
                    assertThat(itemsPage.get(4).title()).isEqualTo("Item 11");
                }).verifyComplete();

        StepVerifier.create(itemService.getItemsPaging(null, 1, 5)).
                consumeNextWith(actualPaging -> {
                    assertThat(actualPaging.pageSize()).isEqualTo(5);
                    assertThat(actualPaging.pageNumber()).isEqualTo(1);
                    assertThat(actualPaging.hasPrevious()).isFalse();
                    assertThat(actualPaging.hasNext()).isTrue();
                }).verifyComplete();
    }

    @Test
    public void getItemsSearchTest() {
        StepVerifier.create(itemService.getItems(DEFAULT_CART_ID, "searchtag", "NO", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(3).title()).isEqualTo("Item 06");
                }).verifyComplete();
    }

    @Test
    public void getItemsSearchOrderByTitleTest() {
        StepVerifier.create(itemService.getItems(DEFAULT_CART_ID, "searchtag", "ALPHA", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(3).title()).isEqualTo("Item 10");
                }).verifyComplete();
    }

    @Test
    public void getItemsSearchOrderByPriceTest() {
        StepVerifier.create(itemService.getItems(DEFAULT_CART_ID, "SEARCHTAG", "PRICE", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(3).title()).isEqualTo("Item 01 searchtag");
                }).verifyComplete();
    }

    @Test
    public void getItemsDefaultTest() {
        StepVerifier.create(itemService.getItems(DEFAULT_CART_ID, null, null, null, null).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(5);
                    assertThat(itemsPage.get(4).title()).isEqualTo("Item 11");
                }).verifyComplete();
    }

    @Test
    void getItemTest() {
        StepVerifier.create(itemService.getItem(DEFAULT_CART_ID, 1L)).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 07 SearchTag")).verifyComplete();
    }

    @Test
    void getItemEmptyTest() {
        StepVerifier.create(itemService.getItem(DEFAULT_CART_ID, 14L)).
                expectNextCount(0).verifyComplete();
    }

    @Test
    void createItem() {
        StepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14))).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 14")).verifyComplete();

        StepVerifier.create(itemService.getItem(DEFAULT_CART_ID, 14L)).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 14")).verifyComplete();
    }
}
