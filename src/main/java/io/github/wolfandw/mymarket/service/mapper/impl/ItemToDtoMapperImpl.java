package io.github.wolfandw.mymarket.service.mapper.impl;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.model.Item;
import io.github.wolfandw.mymarket.service.mapper.ItemToDtoMapper;
import org.springframework.stereotype.Component;

/**
 * Реализация {@link ItemToDtoMapper}.
 */
@Component
public class ItemToDtoMapperImpl implements ItemToDtoMapper {
    @Override
    public ItemDto mapItem(Item item) {
        return new ItemDto(item.getId(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice().longValue());
    }
}
