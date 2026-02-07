package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.*;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Контроллер для работы с товарами.
 */
@Controller
@RequestMapping("/items")
public class ItemController {
    private static final Long DEFAULT_CART_ID = 1L;

    private static final String TEMPLATE_ITEMS = "items";
    private static final String TEMPLATE_ITEM = "item";
    private static final String TEMPLATE_ITEM_NEW = "item_new";

    private static final String ATTRIBUTE_ITEMS = "items";
    private static final String ATTRIBUTE_SEARCH = "search";
    private static final String ATTRIBUTE_SORT = "sort";
    private static final String ATTRIBUTE_PAGING = "paging";
    private static final String ATTRIBUTE_ITEM = "item";
    private static final String ATTRIBUTE_NEW_ITEM = "newItem";
    private static final String ATTRIBUTE_CART_ITEMS_COUNT = "cartItemsCount";
    private static final String ATTRIBUTE_CART_ITEM_COUNT = "cartItemCount";

    private static final String PARAMETER_NEW_ITEM = "newItem";
    private static final String PARAMETER_ACTION = "action";

    private static final String ACTION_PLUS = "PLUS";

    private static final int PAGE_NUMBER_DEFAULT = 1;
    private static final int PAGE_SIZE_DEFAULT = 5;

    private final ItemService itemService;
    private final CartService cartService;
//    private final EntityImageService entityImageService;

    /**
     * Создает экземпляр контроллера товаров.
     *
     * @param itemService сервис товаров
     * @param cartService сервис корзин
//     * @param entityImageService сервис картинок
     */
    public ItemController(ItemService itemService, CartService cartService
            //, EntityImageService entityImageService
    ) {
        this.itemService = itemService;
        this.cartService = cartService;
//        this.entityImageService = entityImageService;
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

        Mono<Long> itemsCount = itemService.getItemsCount(request.getSearch());

        int finalPageNumber = request.getPageNumber() == null ? PAGE_NUMBER_DEFAULT : request.getPageNumber();
        int finalPageSize = request.getPageSize() == null ? PAGE_SIZE_DEFAULT : request.getPageSize();
        Mono<ItemsPagingDto> pagingMono = itemsCount.map(count -> new ItemsPagingDto(finalPageSize,
                finalPageNumber,
                finalPageNumber > 1,
                (long) finalPageNumber * finalPageSize < count));

        Mono<Map<Long, Integer>> cartItemsCount = cartService.getCartItemsCount(DEFAULT_CART_ID);

        return Mono.just(
                Rendering.view(TEMPLATE_ITEMS)
                        .modelAttribute(ATTRIBUTE_ITEMS, itemsDtoPage)
                        .modelAttribute(ATTRIBUTE_SEARCH, request.getSearch())
                        .modelAttribute(ATTRIBUTE_SORT, request.getSort())
                        .modelAttribute(ATTRIBUTE_PAGING, pagingMono)
                        .modelAttribute(ATTRIBUTE_CART_ITEMS_COUNT, cartItemsCount)
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
        Mono<ItemDto> itemDto = itemService.getItem(id);
        Mono<Integer> cartItemCount = cartService.getCartItemCount(DEFAULT_CART_ID, id);
        return Mono.just(
                Rendering.view(TEMPLATE_ITEM)
                        .modelAttribute(ATTRIBUTE_ITEM, itemDto)
                        .modelAttribute(ATTRIBUTE_NEW_ITEM, newItem)
                        .modelAttribute(ATTRIBUTE_CART_ITEM_COUNT, cartItemCount)
                        .build()
        );
    }

//    /**
//     * Изменяет количество товара в корзине на странице товаров и делает на нее редирект.
//     *
//     * @param request запрос на изменение количества с параметрами страницы товаров
//     * @return редирект на страницу товаров с первоначальными значениями параметров
//     */
//    @PostMapping
//    public String changeItemCount(@ModelAttribute ItemsPageChangeCountFormRequest request) {
//        cartService.changeItemCount(DEFAULT_CART_ID, request.getId(),
//                request.getAction());
//
//        String searchParamValue = request.getSearch();
//        String sortParamValue = request.getSort();
//        Integer pageNumberParamValue = request.getPageNumber();
//        Integer pageSizeParamValue = request.getPageSize();
//
//        return RedirectUrlFactory.createRedirectUrlToItems(searchParamValue,
//                        sortParamValue,
//                        pageNumberParamValue,
//                        pageSizeParamValue);
//    }
//
//    /**
//     * Изменяет количество товара в корзине на странице товара.
//     *
//     * @param id идентификатор товара
//     * @param action увеличить (уменьшить)
//     * @return шаблон товара
//     */
//    @PostMapping("/{id}")
//    public String changeItemCount(
//            @PathVariable Long id,
//            @RequestParam(value = PARAMETER_ACTION, defaultValue = ACTION_PLUS)  String action,
//            Model model) {
//        cartService.changeItemCount(DEFAULT_CART_ID, id, action);
//        return getItem(id, false, model);
//    }
//
//    /**
//     * Возвращает страницу создания нового товара.
//     *
//     * @return страница создания товара
//     */
//    @GetMapping("/new")
//    public String addNewItem() {
//        return TEMPLATE_ITEM_NEW;
//    }
//
//    /**
//     * Создает новый товар и возвращает страницу товара.
//     *
//     * @param title название товара
//     * @param description описание товара
//     * @param price цена товара
//     * @param imageFile изображение товара
//     * @return страница созданного товара.
//     */
//    @PostMapping("/new")
//    public String saveNewItem(@RequestParam String title,
//                              @RequestParam String description,
//                              @RequestParam Long price,
//                              @RequestParam MultipartFile imageFile) {
//        ItemDto newItemDto = itemService.createItem(title, description, BigDecimal.valueOf(price));
//        entityImageService.updateEntityImage(newItemDto.id(), imageFile);
//        return RedirectUrlFactory.createRedirectUrlToNewItem(newItemDto.id());
//    }
}
