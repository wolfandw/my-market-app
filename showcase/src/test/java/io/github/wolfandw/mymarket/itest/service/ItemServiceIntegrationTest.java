package io.github.wolfandw.mymarket.itest.service;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleGuest;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест сервиса товаров.
 */
public class ItemServiceIntegrationTest extends AbstractIntegrationTest {
    @Test
    public void getItemsTest() {
        trxStepVerifier.create(itemService.getItems(null, "NO", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(5);
                    assertThat(itemsPage.get(4).title()).isEqualTo("Item 11");
                }).verifyComplete();

        trxStepVerifier.create(itemService.getItemsPaging(null, 1, 5)).
                consumeNextWith(actualPaging -> {
                    assertThat(actualPaging.pageSize()).isEqualTo(5);
                    assertThat(actualPaging.pageNumber()).isEqualTo(1);
                    assertThat(actualPaging.hasPrevious()).isFalse();
                    assertThat(actualPaging.hasNext()).isTrue();
                }).verifyComplete();
    }

    @Test
    public void getItemsSearchTest() {
        trxStepVerifier.create(itemService.getItems("searchtag", "NO", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(3).title()).isEqualTo("Item 06");
                }).verifyComplete();
    }

    @Test
    public void getItemsSearchOrderByTitleTest() {
        trxStepVerifier.create(itemService.getItems("searchtag", "ALPHA", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(3).title()).isEqualTo("Item 10");
                }).verifyComplete();
    }

    @Test
    public void getItemsSearchOrderByPriceTest() {
        trxStepVerifier.create(itemService.getItems("SEARCHTAG", "PRICE", 1, 5).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(3).title()).isEqualTo("Item 01 searchtag");
                }).verifyComplete();
    }

    @Test
    public void getItemsDefaultTest() {
        trxStepVerifier.create(itemService.getItems(null, null, null, null).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(5);
                    assertThat(itemsPage.get(4).title()).isEqualTo("Item 11");
                }).verifyComplete();
    }

    @Test
    public void getItemTest() {
        trxStepVerifier.create(itemService.getItem(1L)).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 07 SearchTag")).verifyComplete();
    }

    @Test
    public void getItemEmptyTest() {
        trxStepVerifier.create(itemService.getItem(99L)).expectNextCount(0).verifyComplete();
    }

    @Test
    @IsRoleAdmin
    void createAdminItem() {
        trxStepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14))).
                consumeNextWith(itemDto -> assertThat(itemDto.title()).isEqualTo("Item 14")).verifyComplete();
    }

    @Test
    @IsRoleUser
    void createUserItem() {
        trxStepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14)))
                .verifyError(AuthorizationDeniedException.class);
    }

    @Test
    void createGuestItem() {
        trxStepVerifier.create(itemService.createItem("Item 14", "Item 14 description", BigDecimal.valueOf(14)))
                .verifyError(AuthorizationDeniedException.class);
    }
}
