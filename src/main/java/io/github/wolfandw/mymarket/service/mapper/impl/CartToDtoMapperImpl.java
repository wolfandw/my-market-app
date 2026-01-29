package io.github.wolfandw.mymarket.service.mapper.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.CartItem;
import io.github.wolfandw.mymarket.service.mapper.CartToDtoMapper;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация {@link CartToDtoMapper}.
 */
@Component
public class CartToDtoMapperImpl implements CartToDtoMapper {
    private final ItemToDtoMapper itemToDtoMapper;

    /**
     * Создает маппер корзины товаров.
     *
     * @param itemToDtoMapper маппер товаров
     */
    public CartToDtoMapperImpl(ItemToDtoMapper itemToDtoMapper) {
        this.itemToDtoMapper = itemToDtoMapper;
    }

    @Override
    public List<ItemDto> mapCartItems(List<CartItem> cartItems) {
        return cartItems.stream().map(ci -> itemToDtoMapper.mapItem(ci.getItem(),
                ci.getCount())).collect(Collectors.toList());
    }
}
