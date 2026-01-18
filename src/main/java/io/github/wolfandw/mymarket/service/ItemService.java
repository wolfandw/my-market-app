package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.ItemsPageDto;

/**
 * Сервис для работы с товарами.
 */
public interface ItemService {
    /**
     * Возвращает страницу товаров.
     *
     * @param search строка поиска
     * @param sort направление сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return DTO-описание страницы товаров
     */
    ItemsPageDto getItemsPage(String search, String sort, Integer pageNumber, Integer pageSize);
}
