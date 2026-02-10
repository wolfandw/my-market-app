package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.CartDto;
import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemPageChangeCountFormRequest;
import io.github.wolfandw.mymarket.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с корзиной товаров.
 */
@Controller
@RequestMapping("/cart/items")
public class CartController {
    private static final Long DEFAULT_CART_ID = 1L;

    private static final String TEMPLATE_CART = "cart";

    private static final String ATTRIBUTE_CART = "cart";
    private static final String ATTRIBUTE_ITEMS = "items";

    private final CartService cartService;

    /**
     * Создает контроллер корзины.
     *
     * @param cartService сервис корзины
     */
    public CartController(CartService cartService
    ) {
        this.cartService = cartService;
    }

    /**
     * Возвращает шаблон и модель формы корзины.
     *
     * @return шаблон корзины
     */
    @GetMapping
    public Mono<Rendering> getCart() {
        Mono<CartDto> cartMono = cartService.getCart(DEFAULT_CART_ID);
        Flux<ItemDto> cartItemsFlux = cartService.getCartItems(DEFAULT_CART_ID);
        return Mono.just(
                Rendering.view(TEMPLATE_CART)
                        .modelAttribute(ATTRIBUTE_CART, cartMono)
                        .modelAttribute(ATTRIBUTE_ITEMS, cartItemsFlux)
                        .build()
        );
    }

    /**
     * Изменяет количество товара в корзине.
     *
     * @param request запрос с идентификатором товара и действием
     * @return шаблон корзины
     */
    @PostMapping
    public Mono<String> changeChartItemCount(@ModelAttribute ItemPageChangeCountFormRequest request) {
        return cartService.changeItemCount(DEFAULT_CART_ID, request.getId(), request.getAction()).
                thenReturn(RedirectUrlFactory.createRedirectUrlToCart(DEFAULT_CART_ID));
    }
}
