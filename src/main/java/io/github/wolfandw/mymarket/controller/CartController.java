package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.MyMarketConstants;
import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.EntityImageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Контроллер для работы с корзиной товаров.
 */
@Controller
@RequestMapping("/cart/items")
public class CartController {
    private static final String TEMPLATE_CART = "cart";

    private static final String ATTRIBUTE_ITEMS = "items";
    private static final String ATTRIBUTE_TOTAL = "total";

    private final CartService cartService;
    private final EntityImageService entityImageService;

    /**
     * Создает контроллер корзины.
     *
     * @param cartService сервис корзины
     * @param entityImageService сервис картинок
     */
    public CartController(CartService cartService, EntityImageService entityImageService) {
        this.cartService = cartService;
        this.entityImageService = entityImageService;
    }

    /**
     * Возвращает шаблон и модель формы корзины.
     *
     * @param model модель формы корзины
     * @return шаблон корзины
     */
    @GetMapping
    public String getCart(Model model) {
        CartDto cart = cartService.getCart(MyMarketConstants.DEFAULT_CART_ID);
        cart.items().forEach(item ->
                item.setImgData(entityImageService.getEntityImageBase64(item.id())));
        model.addAttribute(ATTRIBUTE_ITEMS, cart.items());
        model.addAttribute(ATTRIBUTE_TOTAL, cart.total());
        return TEMPLATE_CART;
    }

    /**
     * Изменяет количество товара в корзине.
     *
     * @param id идентификатор товара
     * @param action действие
     * @param model модель
     * @return шаблон корзины
     */
    @PostMapping
    public String changeChartItemCount(
            @RequestParam(value = "id", required = true, defaultValue = "0") Long id,
            @RequestParam(value = "action", required = true, defaultValue = "PLUS")  String action,
            Model model) {
        cartService.changeItemCount(MyMarketConstants.DEFAULT_CART_ID, id, action);
        return getCart(model);
    }
}
