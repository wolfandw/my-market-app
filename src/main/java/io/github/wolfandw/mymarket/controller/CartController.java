package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для работы с корзиной товаров.
 */
@Controller
@RequestMapping("/cart/items")
public class CartController {
    private static final String TEMPLATE_CART = "cart";

    public static final String ATTRIBUTE_ITEMS = "items";
    public static final String ATTRIBUTE_TOTAL = "total";

    private final CartService cartService;

    /**
     * Создает контроллер корзины.
     *
     * @param cartService сервис корзины
     */
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Возвращает модель формы корзины.
     *
     * @param model модель формы корзины
     * @return шаблон корзины
     */
    @GetMapping
    public String getCart(Model model) {
        CartDto cart = cartService.getCart();
        model.addAttribute(ATTRIBUTE_ITEMS, cart.items());
        model.addAttribute(ATTRIBUTE_TOTAL, cart.total());
        return TEMPLATE_CART;
    }
}
