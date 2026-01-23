package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.MyMarketConstants;
import io.github.wolfandw.mymarket.dto.ItemsPageChangeCountFormRequest;
import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPageFormRequest;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с товарами.
 */
@Controller
@RequestMapping("/items")
public class ItemController {
    private static final String TEMPLATE_ITEMS = "items";
    private static final String TEMPLATE_ITEM = "item";

    private static final String ATTRIBUTE_ITEMS = "items";
    private static final String ATTRIBUTE_SEARCH = "search";
    private static final String ATTRIBUTE_SORT = "sort";
    private static final String ATTRIBUTE_PAGING = "paging";
    private static final String ATTRIBUTE_ITEM = "item";

    private static final String REDIRECT_ITEMS = "redirect:/items?";
    private static final String PARAMETER_SEARCH = "search=";
    private static final String PARAMETER_SORT = "&sort=";
    private static final String PARAMETER_PAGE_NUMBER = "&pageNumber=";
    private static final String PARAMETER_PAGE_SIZE = "&pageSize=";

    private final ItemService itemService;
    private final CartService cartService;

    /**
     * Создает экземпляр контроллера товаров.
     *
     * @param itemService сервис товаров
     * @param cartService сервис корзин
     */
    public ItemController(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
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
        ItemsPageDto itemsPageDto = itemService.getItems(MyMarketConstants.DEFAULT_CART_ID, request.getSearch(),
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
     * Возвращает шаблон и модель формы товара.
     *
     * @param id идентификатор товара
     * @param model модель формы товара
     * @return шаблон товара
     */
    @GetMapping("/{id}")
    public String getItem(@PathVariable Long id, Model model) {
        itemService.getItem(MyMarketConstants.DEFAULT_CART_ID, id).
                ifPresent(item -> model.addAttribute(ATTRIBUTE_ITEM, item));
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
        cartService.changeItemCount(MyMarketConstants.DEFAULT_CART_ID, request.getId(),
                request.getAction());

        StringBuilder redirect = new StringBuilder(REDIRECT_ITEMS);
        redirect.append(PARAMETER_SEARCH);
        if (request.getSearch() != null) {
            redirect.append(request.getSearch());
        }
        redirect.append(PARAMETER_SORT);
        if (request.getSort() != null) {
            redirect.append(request.getSort());
        }
        redirect.append(PARAMETER_PAGE_NUMBER);
        if (request.getPageNumber() != null) {
            redirect.append(request.getPageNumber());
        }
        redirect.append(PARAMETER_PAGE_SIZE);
        if (request.getPageSize() != null) {
            redirect.append(request.getPageSize());
        }
        return redirect.toString();
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
            @RequestParam(value = "action", required = true, defaultValue = "PLUS")  String action,
            Model model) {
        cartService.changeItemCount(MyMarketConstants.DEFAULT_CART_ID, id, action);
        return getItem(id, model);
    }
}
