package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageChangeCountFormRequest;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPageFormRequest;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.EntityImageService;
import io.github.wolfandw.mymarket.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

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

    private static final String PARAMETER_NEW_ITEM = "newItem";
    private static final String PARAMETER_ACTION = "action";

    private static final String ACTION_PLUS = "PLUS";

    private final ItemService itemService;
    private final CartService cartService;
    private final EntityImageService entityImageService;

    /**
     * Создает экземпляр контроллера товаров.
     *
     * @param itemService сервис товаров
     * @param cartService сервис корзин
     * @param entityImageService сервис картинок
     */
    public ItemController(ItemService itemService, CartService cartService, EntityImageService entityImageService) {
        this.itemService = itemService;
        this.cartService = cartService;
        this.entityImageService = entityImageService;
    }

    /**
     * Возвращает шаблон и модель формы товаров.
     *
     * @param request запрос от формы
     * @param model модель формы
     * @return имя шаблона страницы товаров
     */
    @GetMapping
    public String getItems(@ModelAttribute ItemsPageFormRequest request, Model model) {
        ItemsPageDto itemsPageDto = itemService.getItems(DEFAULT_CART_ID, request.getSearch(),
                request.getSort(),
                request.getPageNumber(),
                request.getPageSize());

        itemsPageDto.items().forEach(item ->
                item.setImgData(entityImageService.getEntityImageBase64(item.id())));
        model.addAttribute(ATTRIBUTE_ITEMS, itemsPageDto.items());
        model.addAttribute(ATTRIBUTE_SEARCH, request.getSearch());
        model.addAttribute(ATTRIBUTE_SORT, request.getSort());
        model.addAttribute(ATTRIBUTE_PAGING, itemsPageDto.paging());

        return TEMPLATE_ITEMS;
    }

    /**
     * Возвращает шаблон и модель формы товара.
     *
     * @param id идентификатор товара
     * @param model модель формы товара
     * @return шаблон товара
     */
    @GetMapping("/{id}")
    public String getItem(@PathVariable Long id,
                          @RequestParam(value = PARAMETER_NEW_ITEM, required = false, defaultValue = "false") boolean newItem,
                          Model model) {
        itemService.getItem(DEFAULT_CART_ID, id).
                ifPresent(item -> {
                    item.setImgData(entityImageService.getEntityImageBase64(item.id()));
                    model.addAttribute(ATTRIBUTE_ITEM, item);
                    model.addAttribute(ATTRIBUTE_NEW_ITEM, newItem);
                });
        return TEMPLATE_ITEM;
    }

    /**
     * Изменяет количество товара в корзине на странице товаров и делает на нее редирект.
     *
     * @param request запрос на изменение количества с параметрами страницы товаров
     * @return редирект на страницу товаров с первоначальными значениями параметров
     */
    @PostMapping
    public String changeItemCount(@ModelAttribute ItemsPageChangeCountFormRequest request) {
        cartService.changeItemCount(DEFAULT_CART_ID, request.getId(),
                request.getAction());

        String searchParamValue = request.getSearch();
        String sortParamValue = request.getSort();
        Integer pageNumberParamValue = request.getPageNumber();
        Integer pageSizeParamValue = request.getPageSize();

        return RedirectUrlFactory.createRedirectUrlToItems(searchParamValue,
                        sortParamValue,
                        pageNumberParamValue,
                        pageSizeParamValue);
    }

    /**
     * Изменяет количество товара в корзине на странице товара.
     *
     * @param id идентификатор товара
     * @param action увеличить (уменьшить)
     * @return шаблон товара
     */
    @PostMapping("/{id}")
    public String changeItemCount(
            @PathVariable Long id,
            @RequestParam(value = PARAMETER_ACTION, defaultValue = ACTION_PLUS)  String action,
            Model model) {
        cartService.changeItemCount(DEFAULT_CART_ID, id, action);
        return getItem(id, false, model);
    }

    /**
     * Возвращает страницу создания нового товара.
     *
     * @return страница создания товара
     */
    @GetMapping("/new")
    public String addNewItem() {
        return TEMPLATE_ITEM_NEW;
    }

    /**
     * Создает новый товар и возвращает страницу товара.
     *
     * @param title название товара
     * @param description описание товара
     * @param price цена товара
     * @param imageFile изображение товара
     * @return страница созданного товара.
     */
    @PostMapping("/new")
    public String saveNewItem(@RequestParam String title,
                              @RequestParam String description,
                              @RequestParam Long price,
                              @RequestParam MultipartFile imageFile) {
        ItemDto newItemDto = itemService.createItem(title, description, BigDecimal.valueOf(price));
        entityImageService.updateEntityImage(newItemDto.id(), imageFile);
        return RedirectUrlFactory.createRedirectUrlToNewItem(newItemDto.id());
    }
}
