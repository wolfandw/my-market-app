package io.github.wolfandw.mymarket.test.controller;

import io.github.wolfandw.mymarket.controller.ItemController;
import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.dto.EntityImageDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import io.github.wolfandw.mymarket.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartAsyncData;

/**
 * Модульный тест контроллера товаров.
 */
@WebFluxTest(ItemController.class)
public class ItemControllerTest extends AbstractControllerTest {
    private static final String TEMPLATE_ITEMS = "items";
    private static final String TEMPLATE_ITEM = "item";

    private static final String PARAMETER_ID = "id";
    private static final String PARAMETER_SEARCH = "search";
    private static final String PARAMETER_SORT = "sort";
    private static final String PARAMETER_PAGE_NUMBER = "pageNumber";
    private static final String PARAMETER_PAGE_SIZE = "pageSize";
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_TITLE = "title";
    private static final String PARAMETER_DESCRIPTION = "description";
    private static final String PARAMETER_PRICE = "price";
    private static final String PARAMETER_IMAGE_FILE = "imageFile";

    private static final String ACTION_PLUS = "PLUS";

    @Test
    void getItemsTest() {
        String searchParamValue = "";
        String sortParamValue = "";
        int pageNumberParamValue = 1;
        int pageSizeParamValue = 5;

        List<Item> content = new ArrayList<>(ITEMS.values().stream().limit(5).toList());
        List<ItemDto> itemDtos = mapToItemsDto(content);

        ItemsPagingDto paging = new ItemsPagingDto(pageSizeParamValue, pageNumberParamValue, false, true);

        when(itemService.getItems(DEFAULT_CART_ID, searchParamValue,
                sortParamValue,
                pageNumberParamValue,
                pageSizeParamValue)).thenReturn(Flux.fromIterable(itemDtos));

        when(itemService.getItemsPaging(searchParamValue,
                pageNumberParamValue,
                pageSizeParamValue)).thenReturn(Mono.just(paging));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam(PARAMETER_SEARCH, searchParamValue)
                        .queryParam(PARAMETER_SORT, sortParamValue)
                        .queryParam(PARAMETER_PAGE_NUMBER, pageNumberParamValue)
                        .queryParam(PARAMETER_PAGE_SIZE, pageSizeParamValue)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertTrue(body.contains(TEMPLATE_ITEMS));
                });
    }

    @Test
    void getItemTest() {
        Long itemId = 2L;

        when(itemService.getItem(DEFAULT_CART_ID, itemId)).thenReturn(Mono.just(mapItem(ITEMS.get(itemId), 0)));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/items/2")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Item 08"));
                    assertFalse(body.contains("Поздравляем! Товар успешно создан!"));
                    assertTrue(body.contains(TEMPLATE_ITEM));
                });
    }

    @Test
    void changeItemCountOnItemsTest() {
        long itemId = 1L;
        String searchParamValue = "SearchTag";
        String sortParamValue = "NO";
        Integer pageNumberParamValue = 1;
        Integer pageSizeParamValue = 5;

        when(cartService.changeItemCount(DEFAULT_CART_ID, itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam(PARAMETER_ID, Long.toString(itemId))
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .queryParam(PARAMETER_SEARCH, searchParamValue)
                        .queryParam(PARAMETER_SORT, sortParamValue)
                        .queryParam(PARAMETER_PAGE_NUMBER, pageNumberParamValue.toString())
                        .queryParam(PARAMETER_PAGE_SIZE, pageSizeParamValue.toString())
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItems(searchParamValue, sortParamValue, pageNumberParamValue, pageSizeParamValue)
                );

        verify(cartService).changeItemCount(DEFAULT_CART_ID, itemId, ACTION_PLUS);
    }

    @Test
    void changeItemCountOnItemTest() {
        long itemId = 1L;
        when(itemService.getItem(DEFAULT_CART_ID, itemId)).thenReturn(Mono.just(mapItem(ITEMS.get(itemId), 0)));
        when(cartService.changeItemCount(DEFAULT_CART_ID, itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/items/1")
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItem(itemId)
                );


        verify(cartService).changeItemCount(DEFAULT_CART_ID, itemId, ACTION_PLUS);
    }

    @Test
    void addNewItemTest() {
        webTestClient.get().uri("/items/new")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Новый товар"));
                });
    }

    @Test
    void saveNewItemTest() throws Exception {
        Long itemId = 15L;
        String titleParamValue = "Item 15";
        String descriptionParamValue = "Item 15 description";
        long priceParamValue = 15L;
        when(itemService.createItem(titleParamValue, descriptionParamValue, BigDecimal.valueOf(priceParamValue))).
                thenReturn(Mono.just(new ItemDto(itemId, titleParamValue, descriptionParamValue, priceParamValue, 0)));

        webTestClient.post().uri("/items/new")
                .contentType(MediaType.TEXT_HTML)
                .body(fromFormData(PARAMETER_TITLE, titleParamValue).
                        with(PARAMETER_DESCRIPTION, descriptionParamValue).
                        with(PARAMETER_PRICE, Long.toString(priceParamValue)))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "^\\/items\\/\\d+\\?newItem\\=true"
                );
    }

    @Test
    void getItemImageTest() {
        when(entityImageService.getEntityImage(1L)).thenReturn(Mono.just(new EntityImageDto(1L, new byte[]{1,2,3}, MediaType.IMAGE_PNG)));

        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/items/1/image")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.IMAGE_PNG_VALUE)
                .expectBody(byte[].class)
                .consumeWith(res -> {
                    byte[] body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.length > 0);
                });
    }

    @Test
    void setItemImageTest() {
        Long itemId = 1L;
        String imageName = "14.jpg";

        Mono<FilePart> expectedFilePartMono = Mono.just(getFilePart(imageName));
        webTestClient.post().uri(uriBuilder -> uriBuilder
                        .path("/items/1/image")
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fromMultipartAsyncData(PARAMETER_IMAGE_FILE, expectedFilePartMono, FilePart.class))
                .exchange()
                .expectStatus().isOk();
    }

    private List<ItemDto> mapToItemsDto(List<Item> items) {
        return items.stream().map(item -> mapItem(item, 0)).toList();
    }
}
