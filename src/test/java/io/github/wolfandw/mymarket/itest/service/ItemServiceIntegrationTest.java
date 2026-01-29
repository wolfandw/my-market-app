package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.DtoConstants;
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
        ItemsPageDto itemsPageDto = itemService.getItems(DtoConstants.DEFAULT_CART_ID, null, "NO", 1, 5);

        assertThat(itemsPageDto.search()).isNull();
        assertThat(itemsPageDto.sort()).isEqualTo("NO");

        ItemsPagingDto actualPaging = itemsPageDto.paging();
        assertThat(actualPaging).isNotNull();
        assertThat(actualPaging.pageSize()).isEqualTo(5);
        assertThat(actualPaging.pageNumber()).isEqualTo(1);
        assertThat(actualPaging.hasPrevious()).isFalse();
        assertThat(actualPaging.hasNext()).isTrue();

        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1).get(1).title()).isEqualTo("Item 11");

        // stubs
        assertThat(itemsPageDto.items().get(1).get(2).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(2).title()).isEqualTo("");
    }

    @Test
    public void getItemsSearchTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DtoConstants.DEFAULT_CART_ID, "searchtag", "NO", 1, 5);
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
    public void getItemsSearchOrderByTitleTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DtoConstants.DEFAULT_CART_ID, "searchtag", "ALPHA", 1, 5);
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
    public void getItemsSearchOrderByPriceTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DtoConstants.DEFAULT_CART_ID, "SEARCHTAG", "PRICE", 1, 5);
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
    public void getItemsDefaultTest() {
        ItemsPageDto itemsPageDto = itemService.getItems(DtoConstants.DEFAULT_CART_ID, null, null, null, null);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1).get(1).title()).isEqualTo("Item 11");

        // stubs
        assertThat(itemsPageDto.items().get(1).get(2).id()).isEqualTo(-1L);
        assertThat(itemsPageDto.items().get(1).get(2).title()).isEqualTo("");
    }

    @Test
    void getItemTest() {
        Optional<ItemDto> itemDto = itemService.getItem(DtoConstants.DEFAULT_CART_ID, 1L);
        assertThat(itemDto).isPresent();
        assertThat(itemDto.get().title()).isEqualTo("Item 07 SearchTag");
    }

    @Test
    void getItemEmptyTest() {
        Optional<ItemDto> itemDto = itemService.getItem(DtoConstants.DEFAULT_CART_ID, 14L);
        assertThat(itemDto).isEmpty();
    }

    @Test
    @Transactional
    void createItem() {
        ItemDto itemDto = itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14));
        assertThat(itemDto.title()).isEqualTo("Item 14");

        Optional<ItemDto> itemDto14 = itemService.getItem(DtoConstants.DEFAULT_CART_ID, 14L);
        assertThat(itemDto14).isPresent();
    }
}
