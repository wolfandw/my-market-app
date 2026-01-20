package io.github.wolfandw.mymarket.service.mapper;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;

import java.util.List;

/**
 * Маппер модели товара в DTO-представление товара.
 */
public interface ItemsToItemDtoTriplesMapper {
    /**
     * Преобразует список модельных товаров в список триплетов DTO-товаров.
     *
     * @param items список модельных товаров
     * @return список триплетов DTO-товаров.
     */
    List<List<ItemDto>> mapItems(List<Item> items);
}
