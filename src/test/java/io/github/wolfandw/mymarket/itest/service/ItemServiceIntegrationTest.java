package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.mymarket.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест сервиса товаров.
 */
@SpringBootTest
class ItemServiceIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    protected ItemService itemService;

    @Test
    @Transactional
    public void getItemsPageTest() {
        ItemsPageDto itemsPageDto = itemService.getItemsPage(null, "NO", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
    }

    @Test
    @Transactional
    public void getItemsPageSearchTest() {
        ItemsPageDto itemsPageDto = itemService.getItemsPage("item", "NO", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(1);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
    }

    @Test
    @Transactional
    public void getItemsPageSearchOrderByTitleTest() {
        ItemsPageDto itemsPageDto = itemService.getItemsPage("item", "ALPHA", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(1);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(0).get(0).title()).isEqualTo("01 Item");
        assertThat(itemsPageDto.items().get(0).get(1).title()).isEqualTo("02");
    }

    @Test
    @Transactional
    public void getItemsPageSearchOrderByPriceTest() {
        ItemsPageDto itemsPageDto = itemService.getItemsPage("item", "PRICE", 1, 5);
        assertThat(itemsPageDto.items()).size().isEqualTo(1);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(0).get(0).title()).isEqualTo("02");
        assertThat(itemsPageDto.items().get(0).get(1).title()).isEqualTo("01 Item");
    }

    @Test
    @Transactional
    public void getItemsPageDefaultTest() {
        ItemsPageDto itemsPageDto = itemService.getItemsPage(null, null, null, null);
        assertThat(itemsPageDto.items()).size().isEqualTo(2);
        assertThat(itemsPageDto.items().get(0)).size().isEqualTo(3);
        assertThat(itemsPageDto.items().get(1)).size().isEqualTo(3);
    }
}
