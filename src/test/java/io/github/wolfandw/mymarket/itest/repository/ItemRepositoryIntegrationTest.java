package io.github.wolfandw.mymarket.itest.repository;

import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Интеграционные тесты репозитория товаров.
 */
public class ItemRepositoryIntegrationTest extends AbstractRepositoryIntegrationTest {
    @Test
    void findAllTest() {
        int finalPageNumber = 1;
        int finalPageSize = 5;

        Flux<Item> itemsAllFlux = itemRepository.findAll(Sort.unsorted());
        itemsAllFlux.count().doOnNext(count -> assertThat(count).isEqualTo(13)).block();

        Flux<Item> itemsPageFlux = itemsAllFlux.skip((long) finalPageNumber * finalPageSize).take(finalPageSize);
        itemsPageFlux.collectList().doOnNext(itemsPage -> {
            assertThat(itemsPage).size().isEqualTo(5);
            assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 07 SearchTag");
            assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 08");
            assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 09");
            assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 10");
            assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 11");
        });

        Mono<Long> itemsCount = itemRepository.count();
        itemsCount.doOnNext(count -> assertThat(count).isEqualTo(13)).block();
    }

    @Test
    void findAllOrderByTitleTest() {
        int finalPageNumber = 1;
        int finalPageSize = 5;

        Flux<Item> itemsAllFlux = itemRepository.findAll(Sort.by("title"));
        itemsAllFlux.count().doOnNext(count -> assertThat(count).isEqualTo(13)).block();

        Flux<Item> itemsPageFlux = itemsAllFlux.skip((long) finalPageNumber * finalPageSize).take(finalPageSize);
        itemsPageFlux.collectList().doOnNext(itemsPage -> {
            assertThat(itemsPage).size().isEqualTo(5);
            assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 01 searchtag");
            assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 02");
            assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 03");
            assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 04");
            assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 05");
        });

        Mono<Long> itemsCount = itemRepository.count();
        itemsCount.doOnNext(count -> assertThat(count).isEqualTo(13)).block();
    }

    @Test
    void findAllOrderByPriceTest() {
        int finalPageNumber = 1;
        int finalPageSize = 5;

        Flux<Item> itemsAllFlux = itemRepository.findAll(Sort.by("price"));
        itemsAllFlux.count().doOnNext(count -> assertThat(count).isEqualTo(13)).block();

        Flux<Item> itemsPageFlux = itemsAllFlux.skip((long) finalPageNumber * finalPageSize).take(finalPageSize);
        itemsPageFlux.collectList().doOnNext(itemsPage -> {
            assertThat(itemsPage).size().isEqualTo(5);
            assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 13");
            assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 12");
            assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 11");
            assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 10");
            assertThat(itemsPage.get(4).getTitle()).isEqualTo("Item 09");
        });

        Mono<Long> itemsCount = itemRepository.count();
        itemsCount.doOnNext(count -> assertThat(count).isEqualTo(13)).block();
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseTest() {
        String search = "sEaRcHtAg";
        int finalPageNumber = 1;
        int finalPageSize = 5;

        Flux<Item> itemsAllFlux = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, Sort.unsorted());
        Flux<Item> itemsPageFlux = itemsAllFlux.skip((long) finalPageNumber * finalPageSize).take(finalPageSize);
        itemsPageFlux.collectList().doOnNext(itemsPage -> {
            assertThat(itemsPage).size().isEqualTo(4);
            assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 07 SearchTag");
            assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 10");
            assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 01 searchtag");
            assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 06");
        });

        Mono<Long> itemsCount = itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search);
        itemsCount.doOnNext(count -> assertThat(count).isEqualTo(4)).block();
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByTitleTest() {
        String search = "searchtag";
        int finalPageNumber = 1;
        int finalPageSize = 5;

        Flux<Item> itemsAllFlux = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, Sort.by("title"));
        Flux<Item> itemsPageFlux = itemsAllFlux.skip((long) finalPageNumber * finalPageSize).take(finalPageSize);
        itemsPageFlux.collectList().doOnNext(itemsPage -> {
            assertThat(itemsPage).size().isEqualTo(4);
            assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 01 searchtag");
            assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 06");
            assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 07 SearchTag");
            assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 10");
        });

        Mono<Long> itemsCount = itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search);
        itemsCount.doOnNext(count -> assertThat(count).isEqualTo(4)).block();
    }

    @Test
    void findByTitleContainingOrDescriptionContainingAllIgnoreCaseOrderByPriceTest() {
        String search = "SEARCHTAG";
        int finalPageNumber = 1;
        int finalPageSize = 5;

        Flux<Item> itemsAllFlux = itemRepository.findByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search, Sort.by("price"));
        Flux<Item> itemsPageFlux = itemsAllFlux.skip((long) finalPageNumber * finalPageSize).take(finalPageSize);
        itemsPageFlux.collectList().doOnNext(itemsPage -> {
            assertThat(itemsPage).size().isEqualTo(4);
            assertThat(itemsPage.get(0).getTitle()).isEqualTo("Item 10");
            assertThat(itemsPage.get(1).getTitle()).isEqualTo("Item 07 SearchTag");
            assertThat(itemsPage.get(2).getTitle()).isEqualTo("Item 06");
            assertThat(itemsPage.get(3).getTitle()).isEqualTo("Item 01 searchtag");
        });

        Mono<Long> itemsCount = itemRepository.countByTitleContainingOrDescriptionContainingAllIgnoreCase(search, search);
        itemsCount.doOnNext(count -> assertThat(count).isEqualTo(4)).block();
    }
}