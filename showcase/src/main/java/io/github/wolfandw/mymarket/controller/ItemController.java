package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.*;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.service.ItemService;
import io.github.wolfandw.mymarket.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Контроллер для работы с товарами.
 */
@Controller
@RequestMapping("/items")
public class ItemController {
    private static final String TEMPLATE_ITEMS = "items";
    private static final String TEMPLATE_ITEM = "item";
    private static final String TEMPLATE_ITEM_NEW = "item_new";

    private static final String ATTRIBUTE_ITEMS = "items";
    private static final String ATTRIBUTE_SEARCH = "search";
    private static final String ATTRIBUTE_SORT = "sort";
    private static final String ATTRIBUTE_PAGING = "paging";
    private static final String ATTRIBUTE_ITEM = "item";
    private static final String ATTRIBUTE_NEW_ITEM = "newItem";
    private static final String ATTRIBUTE_USER_INFO = "userInfo";

    private static final String PARAMETER_NEW_ITEM = "newItem";
    private static final String PARAMETER_IMAGE_FILE = "imageFile";

    private final ItemService itemService;
    private final CartService cartService;
    private final EntityImageService entityImageService;
    private final UserService userService;

    /**
     * Создает экземпляр контроллера товаров.
     *
     * @param itemService        сервис товаров
     * @param cartService        сервис корзин
     * @param entityImageService сервис картинок товаров.
     * @param userService         сервис пользователей
     */
    public ItemController(ItemService itemService, CartService cartService, EntityImageService entityImageService,
                          UserService userService) {
        this.itemService = itemService;
        this.cartService = cartService;
        this.entityImageService = entityImageService;
        this.userService = userService;
    }

    /**
     * Рендерит шаблон items и заполняет модель данными.
     *
     * @param request запрос от формы
     * @return рендер шаблона items
     */
    @GetMapping
    public Mono<Rendering> getItems(@ModelAttribute ItemsPageFormRequest request) {
        Flux<ItemDto> itemsDtoPage = itemService.getItems(request.getSearch(),
                request.getSort(),
                request.getPageNumber(),
                request.getPageSize());

        Mono<ItemsPagingDto> pagingMono = itemService.getItemsPaging(request.getSearch(),
                request.getPageNumber(),
                request.getPageSize());

        Mono<UserInfoDto> userInfoMono = userService.getCurrentUserInfo();

        return Mono.just(
                Rendering.view(TEMPLATE_ITEMS)
                        .modelAttribute(ATTRIBUTE_ITEMS, itemsDtoPage)
                        .modelAttribute(ATTRIBUTE_SEARCH, request.getSearch())
                        .modelAttribute(ATTRIBUTE_SORT, request.getSort())
                        .modelAttribute(ATTRIBUTE_PAGING, pagingMono)
                        .modelAttribute(ATTRIBUTE_USER_INFO, userInfoMono)
                        .build()
        );
    }

    /**
     * Рендерит шаблон item и заполняет модель данными.
     *
     * @param id идентификатор товара
     * @return рендер шаблона item
     */
    @GetMapping("/{id}")
    public Mono<Rendering> getItem(@PathVariable Long id,
                                   @RequestParam(value = PARAMETER_NEW_ITEM, required = false, defaultValue = "false") boolean newItem) {
        Mono<ItemDto> itemDtoMono = itemService.getItem(id);
        Mono<UserInfoDto> userInfoMono = userService.getCurrentUserInfo();
        return itemDtoMono.map(itemDto ->
                Rendering.view(TEMPLATE_ITEM)
                        .modelAttribute(ATTRIBUTE_ITEM, itemDtoMono)
                        .modelAttribute(ATTRIBUTE_NEW_ITEM, newItem)
                        .modelAttribute(ATTRIBUTE_USER_INFO, userInfoMono)
                        .build()
        ).defaultIfEmpty(Rendering.redirectTo(RedirectUrlFactory.createUrlToItems()).build());
    }

    /**
     * Изменяет количество товара в корзине на странице товаров и делает на нее редирект.
     *
     * @param request запрос на изменение количества с параметрами страницы товаров
     * @return редирект на страницу товаров с первоначальными значениями параметров
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public Mono<String> changeItemCount(@ModelAttribute ItemsPageChangeCountFormRequest request) {
        String searchParamValue = request.getSearch();
        String sortParamValue = request.getSort();
        Integer pageNumberParamValue = request.getPageNumber();
        Integer pageSizeParamValue = request.getPageSize();
        return cartService.changeUserItemCount(request.getId(), request.getAction()).
                thenReturn(RedirectUrlFactory.createRedirectUrlToItems(searchParamValue,
                        sortParamValue,
                        pageNumberParamValue,
                        pageSizeParamValue));
    }

    /**
     * Изменяет количество товара в корзине на странице товара.
     *
     * @param request запрос на изменение количества товара
     * @return шаблон товара
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{id}")
    public Mono<String> changeItemCount(
            @ModelAttribute ItemPageChangeCountFormRequest request) {
        return cartService.changeUserItemCount(request.getId(), request.getAction()).
                thenReturn(RedirectUrlFactory.createRedirectUrlToItem(request.getId()));
    }

    /**
     * Возвращает страницу создания нового товара.
     *
     * @return страница создания товара
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public Mono<Rendering> addNewItem() {
        Mono<UserInfoDto> userInfoMono = userService.getCurrentUserInfo();
        return Mono.just(Rendering.view(TEMPLATE_ITEM_NEW).modelAttribute(ATTRIBUTE_USER_INFO, userInfoMono).build());
    }

    /**
     * Создает новый товар и возвращает страницу товара.
     *
     * @param request свойства нового товара
     * @return страница созданного товара.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/new")
    public Mono<Rendering> saveNewItem(@ModelAttribute ItemNewFormRequest request) {
        Mono<ItemDto> newItemDtoMono = itemService.createItem(request.getTitle(),
                request.getDescription(), BigDecimal.valueOf(request.getPrice()));
        return newItemDtoMono.map(newItemDto ->
                Rendering.redirectTo(RedirectUrlFactory.createUrlToNewItem(newItemDto.id())).build());
    }

    /**
     * Получает изображение товара.
     *
     * @param id идентификатор товара
     * @return изображение товара
     */
    @GetMapping(value = "/{id}/image")
    public Mono<ResponseEntity<byte[]>> getItemImage(@PathVariable Long id) {
        return entityImageService.getEntityImage(id).map(image ->
                ResponseEntity.ok().contentType(image.getMediaType()).body(image.getData()));
    }

    /**
     * Устанавливает изображение товара.
     *
     * @param id        идентификатор товара
     * @param imageFile файл изображения
     * @return редирект на страницу товара
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(path = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<Rendering> setItemImage(@PathVariable Long id,
                                        @RequestPart(PARAMETER_IMAGE_FILE) Mono<FilePart> imageFile) {
        return entityImageService.setEntityImage(id, imageFile).
                thenReturn(Rendering.redirectTo(RedirectUrlFactory.createUrlToItem(id)).build());
    }
}
