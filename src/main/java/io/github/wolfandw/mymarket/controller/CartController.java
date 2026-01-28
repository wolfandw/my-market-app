package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.MyMarketUtils;
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
        CartDto cart = cartService.getCart(MyMarketUtils.DEFAULT_CART_ID);
        cart.items().forEach(item ->
                item.setImgData(entityImageService.getEntityImageBase64(item.id())));
        model.addAttribute(MyMarketUtils.ATTRIBUTE_ITEMS, cart.items());
        model.addAttribute(MyMarketUtils.ATTRIBUTE_TOTAL, cart.total());
        return MyMarketUtils.TEMPLATE_CART;
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
            @RequestParam(value = MyMarketUtils.PARAMETER_ID, defaultValue = "0") Long id,
            @RequestParam(value = MyMarketUtils.PARAMETER_ACTION, defaultValue = MyMarketUtils.ACTION_PLUS)  String action,
            Model model) {
        cartService.changeItemCount(MyMarketUtils.DEFAULT_CART_ID, id, action);
        return getCart(model);
    }
}
