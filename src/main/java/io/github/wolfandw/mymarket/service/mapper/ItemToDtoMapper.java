package io.github.wolfandw.mymarket.service.mapper;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;

import java.util.List;
import java.util.Map;

/**
 * Маппер модели товара в DTO-представление.
 */
public interface ItemToDtoMapper {
    /**
     * Преобразует список модельных товаров в DTO-товары с количеством
     * и в виде триплетов.
     *
     * @param items модельные товары
     * @param itemsCartCount количество товара в корзине
     * @return DTO-товары с количеством и в виде триплетов
     */
    List<List<ItemDto>> mapToTriples(List<Item> items, Map<Long, Integer> itemsCartCount);

    /**
     * Преобразует модельный товар в DTO-товар с количеством.
     *
     * @param item модельный товар
     * @param count количество модельных товаров
     * @return DTO-товар с количеством
     */
    ItemDto mapItem(Item item, int count);
}
