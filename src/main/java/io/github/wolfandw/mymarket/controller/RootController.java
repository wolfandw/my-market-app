package io.github.wolfandw.mymarket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Контроллер для редиректа на страницу работы с товарами.
 */
@Controller
@RequestMapping("/")
public class RootController {
    private static final String REDIRECT_TO_ITEMS = "redirect:/items";

    /**
     * Редирект на страницу товаров.
     *
     * @return строка редиректа для Spring
     */
    @GetMapping
    public String redirectToItems() {
        return REDIRECT_TO_ITEMS;
    }
}
