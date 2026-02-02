package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория товаров.
 */
public class ItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0,5, Sort.unsorted());

        Page<Item> actualPage = itemRepository.findAll(pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(5);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("Item 07 SearchTag");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("Item 08");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("Item 09");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("Item 10");
        assertThat(actualContent.get(4).getTitle()).isEqualTo("Item 11");
    }

    @Test
    void findAllOrderByTitleTest() {
        Pageable pageable = PageRequest.of(0,5, Sort.by("title"));

        Page<Item> actualPage = itemRepository.findAll(pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(5);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("Item 01 searchtag");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("Item 02");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("Item 03");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("Item 04");
        assertThat(actualContent.get(4).getTitle()).isEqualTo("Item 05");
    }

    @Test
    void findAllOrderByPriceTest() {
        Pageable pageable = PageRequest.of(0,5, Sort.by("price"));

        Page<Item> actualPage = itemRepository.findAll(pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(5);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("Item 13");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("Item 12");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("Item 11");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("Item 10");
        assertThat(actualContent.get(4).getTitle()).isEqualTo("Item 09");
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseTest() {
        String search = "sEaRcHtAg";
        Pageable pageable = PageRequest.of(0,5, Sort.unsorted());

        Page<Item> actualPage = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(4);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("Item 07 SearchTag");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("Item 10");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("Item 01 searchtag");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("Item 06");
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByTitleTest() {
        String search = "searchtag";
        Pageable pageable = PageRequest.of(0,5, Sort.by("title"));

        Page<Item> actualPage = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(4);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("Item 01 searchtag");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("Item 06");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("Item 07 SearchTag");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("Item 10");
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByPriceTest() {
        String search = "SEARCHTAG";
        Pageable pageable = PageRequest.of(0,5, Sort.by("price"));

        Page<Item> actualPage = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(4);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("Item 10");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("Item 07 SearchTag");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("Item 06");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("Item 01 searchtag");
    }
}