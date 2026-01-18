package io.github.wolfandw.mymarket.dto;

import java.util.List;

/**
 * DTO-описание страницы товаров.
 *
 * @param items список списков товаров по три
 * @param search строка поиска
 * @param sort направление сортировки
 * @param paging пагинация
 */
public record ItemsPageDto(List<List<ItemDto>> items,
                           String search,
                           String sort,
                           ItemsPagingDto paging) {
}
