package io.github.wolfandw.mymarket.itest.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория товаров.
 */
public class ItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());

        StepVerifier.create(itemRepository.findAllBy(pageable).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(5);
                    assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 07 SearchTag");
                    assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 08");
                    assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 09");
                    assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 10");
                    assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 11");
                }).verifyComplete();

        StepVerifier.create(itemRepository.count()).
                assertNext(count -> assertThat(count).isEqualTo(13)).verifyComplete();
    }

    @Test
    void findAllOrderByTitleTest() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title"));

        StepVerifier.create(itemRepository.findAllBy(pageable).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(5);
                    assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 01 searchtag");
                    assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 02");
                    assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 03");
                    assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 04");
                    assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 05");
                }).verifyComplete();

        StepVerifier.create(itemRepository.count()).
                assertNext(count -> assertThat(count).isEqualTo(13)).verifyComplete();
    }

    @Test
    void findAllOrderByPriceTest() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by("price"));

        StepVerifier.create(itemRepository.findAllBy(pageable).collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(5);
                    assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 13");
                    assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 12");
                    assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 11");
                    assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 10");
                    assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 09");
                }).verifyComplete();

        StepVerifier.create(itemRepository.count()).
                assertNext(count -> assertThat(count).isEqualTo(13)).verifyComplete();
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseTest() {
        String search = "sEaRcHtAg";
        Pageable pageable = PageRequest.of(0, 5, Sort.unsorted());

        StepVerifier.create(itemRepository.
                        findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable).
                        collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 07 SearchTag");
                    assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 10");
                    assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 01 searchtag");
                    assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 06");
                }).verifyComplete();

        StepVerifier.create(itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search)).
                assertNext(count -> assertThat(count).isEqualTo(4)).verifyComplete();
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByTitleTest() {
        String search = "searchtag";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("title"));

        StepVerifier.create(itemRepository.
                        findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable).
                        collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 01 searchtag");
                    assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 06");
                    assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 07 SearchTag");
                    assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 10");
                }).verifyComplete();

        StepVerifier.create(itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search)).
                assertNext(count -> assertThat(count).isEqualTo(4)).verifyComplete();
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByPriceTest() {
        String search = "SEARCHTAG";
        Pageable pageable = PageRequest.of(0, 5, Sort.by("price"));

        StepVerifier.create(itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, pageable).
                        collectList()).
                assertNext(itemsPage -> {
                    assertThat(itemsPage).size().isEqualTo(4);
                    assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 10");
                    assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 07 SearchTag");
                    assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 06");
                    assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 01 searchtag");
                }).verifyComplete();

        StepVerifier.create(itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search)).
                assertNext(count -> assertThat(count).isEqualTo(4)).verifyComplete();
    }
}