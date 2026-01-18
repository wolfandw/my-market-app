package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.ItemsPageDto;
import io.github.wolfandw.mymarket.dto.ItemsPageFormRequest;
import io.github.wolfandw.mymarket.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для работы с товарами.
 */
@Controller
@RequestMapping("/items")
public class ItemController {
    private static final String TEMPLATE_ITEMS = "items";

    private final ItemService itemService;

    /**
     * Создает экземпляр контроллера товаров.
     *
     * @param itemService сервис товаров
     */
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String getItemsPage(@ModelAttribute ItemsPageFormRequest request, Model model) {
        ItemsPageDto itemsPageDto = itemService.getItemsPage(request.getSearch(),
                request.getSort(),
                request.getPageNumber(),
                request.getPageSize());

        model.addAttribute("items", itemsPageDto.items());
        model.addAttribute("search", request.getSearch());
        model.addAttribute("sort", request.getSort());
        model.addAttribute("paging", itemsPageDto.paging());

        return TEMPLATE_ITEMS;
    }
}
