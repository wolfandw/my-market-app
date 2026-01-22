package io.github.wolfandw.mymarket.service.mapper;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;

import java.util.List;
import java.util.Map;

/**
 * Маппер модели товара в DTO-представление товара.
 */
public interface ItemToDtoMapper {
    /**
     * Преобразует список модельных товаров в DTO-товары с количеством.
     *
     * @param items модельные товары
     * @param itemsCount количество
     * @return DTO-товары с количеством
     */
    List<ItemDto> mapItems(List<Item> items, Map<Long, Integer> itemsCount);

    /**
     * Преобразует модельный товар в DTO-товар с количеством.
     *
     * @param item модельный товар
     * @param count количество модельных товаров
     * @return DTO-товар с количеством
     */
    ItemDto mapItem(Item item, int count);
}
