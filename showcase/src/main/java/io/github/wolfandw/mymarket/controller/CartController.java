package io.github.wolfandw.mymarket.controller;

import io.github.wolfandw.mymarket.dto.ItemPageChangeCountFormRequest;
import io.github.wolfandw.mymarket.dto.UserInfoDto;
import io.github.wolfandw.mymarket.service.CartService;
import io.github.wolfandw.mymarket.service.PaymentsService;
import io.github.wolfandw.mymarket.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

/**
 * Контроллер для работы с корзиной товаров.
 */
@Controller
@RequestMapping("/cart/items")
public class CartController {
    private static final String TEMPLATE_CART = "cart";

    private static final String ATTRIBUTE_CART = "cart";
    private static final String ATTRIBUTE_ITEMS = "items";
    private static final String ATTRIBUTE_BALANCE = "balance";
    private static final String ATTRIBUTE_USER_INFO = "userInfo";

    private final CartService cartService;
    private final PaymentsService paymentsService;
    private final UserService userService;

    /**
     * Создает контроллер корзины.
     *
     * @param cartService     сервис корзины
     * @param paymentsService сервис платежей
     * @param userService сервис пользователей
     */
    public CartController(CartService cartService, PaymentsService paymentsService,
                          UserService userService) {
        this.cartService = cartService;
        this.paymentsService = paymentsService;
        this.userService = userService;
    }

    /**
     * Возвращает шаблон и модель формы корзины.
     *
     * @return шаблон корзины
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public Mono<Rendering> getCart() {
        Mono<UserInfoDto> userInfoMono = userService.getCurrentUserInfo();
        return Mono.just(Rendering.view(TEMPLATE_CART)
                .modelAttribute(ATTRIBUTE_CART, cartService.getUserCart())
                .modelAttribute(ATTRIBUTE_ITEMS, cartService.getUserCartItems())
                .modelAttribute(ATTRIBUTE_BALANCE, paymentsService.getUserBalance())
                .modelAttribute(ATTRIBUTE_USER_INFO, userInfoMono)
                .build()
        );
    }

    /**
     * Изменяет количество товара в корзине.
     *
     * @param request запрос с идентификатором товара и действием
     * @return шаблон корзины
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public Mono<String> changeChartItemCount(@ModelAttribute ItemPageChangeCountFormRequest request) {
        return cartService.changeUserItemCount(request.getId(), request.getAction()).
                thenReturn(RedirectUrlFactory.createRedirectUrlToUserCart());
    }
}
