package io.github.wolfandw.mymarket.service.mapper;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;

/**
 * Маппер модели товара в DTO-представление.
 */
public interface ItemToDtoMapper {
    /**
     * Преобразует модельный товар в DTO-товар с количеством.
     *
     * @param item модельный товар
     * @param count количество модельных товаров
     * @return DTO-товар с количеством
     */
    ItemDto mapItem(Item item, int count);
}
