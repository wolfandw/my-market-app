package io.github.wolfandw.mymarket.service.mapper;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;

/**
 * Маппер модели товара в DTO-представление.
 */
public interface ItemToDtoMapper {
    /**
     * Преобразует модельный товар в DTO-товар.
     *
     * @param item модельный товар
     * @return DTO-товар
     */
    ItemDto mapItem(Item item);
}
