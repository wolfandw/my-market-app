package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.MyMarketConstants;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты сервиса товаров.
 */
class ItemServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    @Transactional
    public void getItemsTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(MyMarketConstants.DEFAULT_CART_ID, null, "NO", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1).get(1).title()).isEqualTo("Item 11");

        // stubs
        assertThat(itemsPageDto.items().get(1).get(2).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(2).title()).isEqualTo("");
    }

    @Test
    @Transactional
    public void getItemsSearchTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(MyMarketConstants.DEFAULT_CART_ID, "searchtag", "NO", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1).get(0).title()).isEqualTo("Item 06");

        // stubs
        assertThat(itemsPageDto.items().get(1).get(1).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(1).title()).isEqualTo("");

        assertThat(itemsPageDto.items().get(1).get(2).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(2).title()).isEqualTo("");
    }

    @Test
    @Transactional
    public void getItemsSearchOrderByTitleTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(MyMarketConstants.DEFAULT_CART_ID, "searchtag", "ALPHA", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1).get(0).title()).isEqualTo("Item 10");

        // stubs
        assertThat(itemsPageDto.items().get(1).get(1).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(1).title()).isEqualTo("");

        assertThat(itemsPageDto.items().get(1).get(2).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(2).title()).isEqualTo("");
    }

    @Test
    @Transactional
    public void getItemsSearchOrderByPriceTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(MyMarketConstants.DEFAULT_CART_ID, "SEARCHTAG", "PRICE", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1).get(0).title()).isEqualTo("Item 01 searchtag");

        // stubs
        assertThat(itemsPageDto.items().get(1).get(1).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(1).title()).isEqualTo("");

        assertThat(itemsPageDto.items().get(1).get(2).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(2).title()).isEqualTo("");
    }

    @Test
    @Transactional
    public void getItemsDefaultTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(MyMarketConstants.DEFAULT_CART_ID, null, null, null, null);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1).get(1).title()).isEqualTo("Item 11");

        // stubs
        assertThat(itemsPageDto.items().get(1).get(2).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(2).title()).isEqualTo("");
    }
}
