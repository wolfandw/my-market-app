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
import org.springframework.security.test.context.support.WithMockUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
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

    private void checkItems() {
        String searchParamValue = "";
        String sortParamValue = "";
        int pageNumberParamValue = 1;
        int pageSizeParamValue = 5;

        List<Item> content = new ArrayList<>(ITEMS.values().stream().limit(5).toList());
        List<ItemDto> itemDtos = mapToItemsDto(content);

        ItemsPagingDto paging = new ItemsPagingDto(pageSizeParamValue, pageNumberParamValue, false, true);

        when(itemService.getItems(searchParamValue,
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
    @WithMockUser(roles = "ANONYMOUS")
    void getItemsGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());
        checkItems();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getItemsUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
        checkItems();
    }

   private void checkItem() {
        Long itemId = 2L;

        when(itemService.getItem(itemId)).thenReturn(Mono.just(mapItem(ITEMS.get(itemId), 0)));

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
    @WithMockUser(roles = "ANONYMOUS")
    void getItemGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());
        checkItem();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getItemUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
        checkItem();
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    void changeItemCountOnItemsGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());

        long itemId = 1L;
        String searchParamValue = "SearchTag";
        String sortParamValue = "NO";
        Integer pageNumberParamValue = 1;
        Integer pageSizeParamValue = 5;

        when(cartService.changeUserItemCount(itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
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

        verify(cartService).changeUserItemCount(itemId, ACTION_PLUS);
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeItemCountOnItemsUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());

        long itemId = 1L;
        String searchParamValue = "SearchTag";
        String sortParamValue = "NO";
        Integer pageNumberParamValue = 1;
        Integer pageSizeParamValue = 5;

        when(cartService.changeUserItemCount(itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
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

        verify(cartService).changeUserItemCount(itemId, ACTION_PLUS);
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    void changeItemCountOnItemGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());

        long itemId = 1L;
        when(itemService.getItem(itemId)).thenReturn(Mono.just(mapItem(ITEMS.get(itemId), 0)));
        when(cartService.changeUserItemCount(itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
                        .path("/items/1")
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItem(itemId)
                );


        verify(cartService).changeUserItemCount(itemId, ACTION_PLUS);
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeItemCountOnItemUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());

        long itemId = 1L;
        when(itemService.getItem(itemId)).thenReturn(Mono.just(mapItem(ITEMS.get(itemId), 0)));
        when(cartService.changeUserItemCount(itemId, ACTION_PLUS)).thenReturn(Mono.empty());

        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
                        .path("/items/1")
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItem(itemId)
                );


        verify(cartService).changeUserItemCount(itemId, ACTION_PLUS);
    }

    @Test
    @WithMockUser(roles = "ANONYMOUS")
    void addNewItemGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());
        webTestClient.get().uri("/items/new")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .consumeWith(res -> {
                    String body = res.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Для незарегистрированных пользователей функционал ограничен! Корзина не может быть наполнена!"));
                });
    }

    @Test
    @WithMockUser(roles = "USER")
    void addNewItemUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
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
    @WithMockUser(roles = "ADMIN")
    void addNewItemAdminTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getAdminInfo());
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
    @WithMockUser(roles = "ANONYMOUS")
    void saveNewItemGuestTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());
        Long itemId = 15L;
        String titleParamValue = "Item 15";
        String descriptionParamValue = "Item 15 description";
        long priceParamValue = 15L;
        when(itemService.createItem(titleParamValue, descriptionParamValue, BigDecimal.valueOf(priceParamValue))).
                thenReturn(Mono.just(new ItemDto(itemId, titleParamValue, descriptionParamValue, priceParamValue, 0)));

        webTestClient.mutateWith(csrf())
                .post().uri("/items/new")
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
    @WithMockUser(roles = "USER")
    void saveNewItemUserTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
        Long itemId = 15L;
        String titleParamValue = "Item 15";
        String descriptionParamValue = "Item 15 description";
        long priceParamValue = 15L;
        when(itemService.createItem(titleParamValue, descriptionParamValue, BigDecimal.valueOf(priceParamValue))).
                thenReturn(Mono.just(new ItemDto(itemId, titleParamValue, descriptionParamValue, priceParamValue, 0)));

        webTestClient.mutateWith(csrf())
                .post().uri("/items/new")
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
    @WithMockUser(roles = "ADMIN")
    void saveNewItemAdminTest() throws Exception {
        when(userService.getCurrentUserInfo()).thenReturn(getAdminInfo());
        Long itemId = 15L;
        String titleParamValue = "Item 15";
        String descriptionParamValue = "Item 15 description";
        long priceParamValue = 15L;
        when(itemService.createItem(titleParamValue, descriptionParamValue, BigDecimal.valueOf(priceParamValue))).
                thenReturn(Mono.just(new ItemDto(itemId, titleParamValue, descriptionParamValue, priceParamValue, 0)));

        webTestClient.mutateWith(csrf())
                .post().uri("/items/new")
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
    @WithMockUser(roles = "ANONYMOUS")
    void getItemImageGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());
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
    @WithMockUser(roles = "USER")
    void getItemImageUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());
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
    @WithMockUser(roles = "ANONYMOUS")
    void setItemImageGuestTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getGuestInfo());

        Long itemId = 1L;
        String imageName = "14.jpg";

        Mono<FilePart> expectedFilePartMono = Mono.just(getFilePart(imageName));
        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
                        .path("/items/1/image")
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fromMultipartAsyncData(PARAMETER_IMAGE_FILE, expectedFilePartMono, FilePart.class))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "USER")
    void setItemImageUserTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getUserInfo());

        Long itemId = 1L;
        String imageName = "14.jpg";

        Mono<FilePart> expectedFilePartMono = Mono.just(getFilePart(imageName));
        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
                        .path("/items/1/image")
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fromMultipartAsyncData(PARAMETER_IMAGE_FILE, expectedFilePartMono, FilePart.class))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setItemImageAdminTest() {
        when(userService.getCurrentUserInfo()).thenReturn(getAdminInfo());

        Long itemId = 1L;
        String imageName = "14.jpg";

        Mono<FilePart> expectedFilePartMono = Mono.just(getFilePart(imageName));
        webTestClient.mutateWith(csrf())
                .post().uri(uriBuilder -> uriBuilder
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
