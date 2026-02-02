package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест сервиса товаров.
 */
public class ItemServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void getItemsTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DEFAULT_CART_ID, null, "NO", 1, 5);

        assertThat(itemsPageDto.search()).isNull();
        assertThat(itemsPageDto.sort()).isEqualTo("NO");

        ItemsPagingDto actualPaging = itemsPageDto.paging();
        assertThat(actualPaging).isNotNull();
        assertThat(actualPaging.pageSize()).isEqualTo(5);
        assertThat(actualPaging.pageNumber()).isEqualTo(1);
        assertThat(actualPaging.hasPrevious()).isFalse();
        assertThat(actualPaging.hasNext()).isTrue();

        assertThat(itemsPageDto.items()).size().isEqualTo(5);
        assertThat(itemsPageDto.items().get(4).title()).isEqualTo("Item 11");
    }

    @Test
    public void getItemsSearchTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DEFAULT_CART_ID, "searchtag", "NO", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(4);
        assertThat(itemsPageDto.items().get(3).title()).isEqualTo("Item 06");
    }

    @Test
    public void getItemsSearchOrderByTitleTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DEFAULT_CART_ID, "searchtag", "ALPHA", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(4);
        assertThat(itemsPageDto.items().get(3).title()).isEqualTo("Item 10");
    }

    @Test
    public void getItemsSearchOrderByPriceTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DEFAULT_CART_ID, "SEARCHTAG", "PRICE", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(4);
        assertThat(itemsPageDto.items().get(3).title()).isEqualTo("Item 01 searchtag");
    }

    @Test
    public void getItemsDefaultTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DEFAULT_CART_ID, null, null, null, null);
        assertThat(itemsPageDto.items()).size().isEqualTo(5);
        assertThat(itemsPageDto.items().get(4).title()).isEqualTo("Item 11");
    }

    @Test
    void getItemTest() {
        Optional<ItemDto> itemDto = itemService.getItem(DEFAULT_CART_ID, 1L);
        assertThat(itemDto).isPresent();
        assertThat(itemDto.get().title()).isEqualTo("Item 07 SearchTag");
    }

    @Test
    void getItemEmptyTest() {
        Optional<ItemDto> itemDto = itemService.getItem(DEFAULT_CART_ID, 14L);
        assertThat(itemDto).isEmpty();
    }

    @Test
    @Transactional
    void createItem() {
        ItemDto itemDto = itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14));
        assertThat(itemDto.title()).isEqualTo("Item 14");

        Optional<ItemDto> itemDto14 = itemService.getItem(DEFAULT_CART_ID, 14L);
        assertThat(itemDto14).isPresent();
    }
}
