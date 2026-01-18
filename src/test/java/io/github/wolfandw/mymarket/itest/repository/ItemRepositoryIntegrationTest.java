package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционный тест репозитория товаров.
 */
@DataJpaTest
public class ItemRepositoryIntegrationTest extends AbstractIntegrationTest {
    @Test
    void findAllTest() {
        String search = "item";
        Pageable pageable = PageRequest.of(0,5, Sort.unsorted());

        Page<Item> actualPage = itemRepository.findAll(pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(4);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("02");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("03");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("01 Item");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("04");
    }

    @Test
    void findAllOrderByTitleTest() {
        String search = "item";
        Pageable pageable = PageRequest.of(0,5, Sort.by("title"));

        Page<Item> actualPage = itemRepository.findAll(pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(4);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("01 Item");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("02");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("03");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("04");
    }

    @Test
    void findAllOrderByPriceTest() {
        String search = "item";
        Pageable pageable = PageRequest.of(0,5, Sort.by("price"));

        Page<Item> actualPage = itemRepository.findAll(pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(4);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("02");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("03");
        assertThat(actualContent.get(2).getTitle()).isEqualTo("01 Item");
        assertThat(actualContent.get(3).getTitle()).isEqualTo("04");
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseTest() {
        String search = "item";
        Pageable pageable = PageRequest.of(0,5, Sort.unsorted());

        Page<Item> actualPage = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(2);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("02");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("01 Item");
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByTitleTest() {
        String search = "item";
        Pageable pageable = PageRequest.of(0,5, Sort.by("title"));

        Page<Item> actualPage = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(2);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("01 Item");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("02");
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByPriceTest() {
        String search = "item";
        Pageable pageable = PageRequest.of(0,5, Sort.by("price"));

        Page<Item> actualPage = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable);
        List<Item> actualContent = actualPage.getContent();

        assertThat(actualContent).size().isEqualTo(2);
        assertThat(actualContent.get(0).getTitle()).isEqualTo("02");
        assertThat(actualContent.get(1).getTitle()).isEqualTo("01 Item");
    }
}
