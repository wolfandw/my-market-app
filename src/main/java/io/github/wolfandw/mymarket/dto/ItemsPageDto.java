package io.github.wolfandw.mymarket.dto;

import java.util.List;

/**
 * DTO-описание страницы товаров.
 *
 * @param items список товаров
 * @param search строка поиска
 * @param sort направление сортировки
 * @param paging пагинация
 */
public record ItemsPageDto(List<ItemDto> items,
                           String search,
                           String sort,
                           ItemsPagingDto paging) {
}
