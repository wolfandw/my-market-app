package io.github.wolfandw.mymarket.service.mapper;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.CartItem;

import java.util.List;

/**
 * Маппер модели строк корзины товаров в DTO-представление товара.
 */
public interface CartItemToDtoMapper {
    /**
     * Преобразует список строк корзины товаров в DTO-товары с количеством.
     *
     * @param cartItems строки корзины товаров
     * @return DTO-товары с количеством
     */
    List<ItemDto> mapCartItems(List<CartItem> cartItems);
}
