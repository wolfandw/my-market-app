package io.github.wolfandw.mymarket.itest.controller;

import io.github.wolfandw.mymarket.IsRoleAdmin;
import io.github.wolfandw.mymarket.IsRoleUser;
import io.github.wolfandw.mymarket.controller.RedirectUrlFactory;
import io.github.wolfandw.mymarket.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;
import static org.springframework.web.reactive.function.BodyInserters.fromMultipartAsyncData;

/**
 * Интеграционные тесты контроллера товаров.
 */
public class ItemControllerIntegrationTest extends AbstractIntegrationTest {
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
    @IsRoleUser
    public void getItemsUserTest() {
        getItemsTest();
    }

    @Test
    public void getItemsTest() {
        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/items")
                        .queryParam(PARAMETER_SEARCH, "")
                        .queryParam(PARAMETER_SORT, "")
                        .queryParam(PARAMETER_PAGE_NUMBER, "")
                        .queryParam(PARAMETER_PAGE_SIZE, "")
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
    @IsRoleUser
    public void getItemUserTest() {
        getItemTest();
    }

    @Test
    public void getItemTest() {
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
    @IsRoleUser
    public void changeItemCountOnItemsUserTest() {
        long itemId = 1L;
        String searchParamValue = "SearchTag";
        String sortParamValue = "NO";
        Integer pageNumberParamValue = 1;
        Integer pageSizeParamValue = 5;

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
    }

    @Test
    public void changeItemCountOnItemTest() {
        webTestClient.mutateWith(csrf()).post().uri(uriBuilder -> uriBuilder
                        .path("/items/1")
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        "/login"
                );
    }

    @Test
    @IsRoleUser
    public void changeItemCountOnItemUserTest() {
        webTestClient.mutateWith(csrf()).post().uri(uriBuilder -> uriBuilder
                        .path("/items/1")
                        .queryParam(PARAMETER_ACTION, ACTION_PLUS)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(
                        "Location",
                        RedirectUrlFactory.createUrlToItem(1L)
                );
    }

    @Test
    public void addNewItemTest() {
        webTestClient.get().uri("/items/add/new")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueMatches(
                        "Location",
                        "/login"
                );
    }

    @Test
    @IsRoleAdmin
    public void saveNewItemAdminTest() {
        webTestClient.mutateWith(csrf()).post().uri("/items/add/new")
                .contentType(MediaType.TEXT_HTML)
                .body(fromFormData(PARAMETER_TITLE, "Item").
                        with(PARAMETER_DESCRIPTION, "Item description").
                        with(PARAMETER_PRICE, "999"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "^\\/items\\/\\d+\\?newItem\\=true"
                );
    }

    @Test
    public void saveNewItemTest() {
        webTestClient.mutateWith(csrf()).post().uri("/items/add/new")
                .contentType(MediaType.TEXT_HTML)
                .body(fromFormData(PARAMETER_TITLE, "Item").
                        with(PARAMETER_DESCRIPTION, "Item description").
                        with(PARAMETER_PRICE, "999"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches(
                        "Location",
                        "/login"
                );
    }

    @Test
    public void getItemImageTest() {
        setupImages();
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
        cleanUpImages();
    }

    @Test
    @IsRoleAdmin
    public void setItemImageAdminTest() {
        String imageName = "14.jpg";

        setupImages();
        Mono<FilePart> expectedFilePartMono = getFilePart(imageName);
        webTestClient.mutateWith(csrf()).post().uri(uriBuilder -> uriBuilder
                        .path("/items/1/image")
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fromMultipartAsyncData(PARAMETER_IMAGE_FILE, expectedFilePartMono, FilePart.class))
                .exchange()
                .expectStatus().isOk();
        cleanUpImages();
    }

    @Test
    public void setItemImageTest() {
        String imageName = "14.jpg";
        Mono<FilePart> expectedFilePartMono = getFilePart(imageName);
        webTestClient.mutateWith(csrf()).post().uri(uriBuilder -> uriBuilder
                        .path("/items/1/image")
                        .build())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fromMultipartAsyncData(PARAMETER_IMAGE_FILE, expectedFilePartMono, FilePart.class))
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueMatches(
                        "Location",
                        "/login"
                );
        cleanUpImages();
    }
}
