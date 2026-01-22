package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPageFormRequest;
import io.github.wolfandw.mymarket.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для работы с товарами.
 */
@Controller
@RequestMapping("/items")
public class ItemController {
    private static final String TEMPLATE_ITEMS = "items";
    private static final String TEMPLATE_ITEM = "item";

    public static final String ATTRIBUTE_ITEMS = "items";
    public static final String ATTRIBUTE_SEARCH = "search";
    public static final String ATTRIBUTE_SORT = "sort";
    public static final String ATTRIBUTE_PAGING = "paging";
    public static final String ATTRIBUTE_ITEM = "item";

    private final ItemService itemService;

    /**
     * Создает экземпляр контроллера товаров.
     *
     * @param itemService сервис товаров
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Возвращает модель формы товаров.
     *
     * @param request запрос от формы
     * @param model модель формы
     * @return имя шаблона страницы товаров
     */
    @GetMapping
    public String getItemsPage(@ModelAttribute ItemsPageFormRequest request, Model model) {
        ItemsPageDto itemsPageDto = itemService.getItemsPage(request.getSearch(),
                request.getSort(),
                request.getPageNumber(),
                request.getPageSize());

        model.addAttribute(ATTRIBUTE_ITEMS, itemsPageDto.items());
        model.addAttribute(ATTRIBUTE_SEARCH, request.getSearch());
        model.addAttribute(ATTRIBUTE_SORT, request.getSort());
        model.addAttribute(ATTRIBUTE_PAGING, itemsPageDto.paging());

        return TEMPLATE_ITEMS;
    }

    /**
     * Возвращает модель формы товара.
     * @param id идентификатор товара
     * @param model модель формы товара
     * @return шаблон товара
     */
    @GetMapping("/{id}")
    public String getItemPage(@PathVariable Long id, Model model) {
        ItemDto item = itemService.getItem(id);
        model.addAttribute(ATTRIBUTE_ITEM, item);
        return TEMPLATE_ITEM;
    }
}
