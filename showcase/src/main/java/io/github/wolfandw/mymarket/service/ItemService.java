package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import io.github.wolfandw.mymarket.dto.ItemsPagingDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Сервис для работы с товарами.
 */
public interface ItemService {
    /**
     * Возвращает список DTO-товаров по размеру страницы.
     *
     * @param cartId идентификатор корзины
     * @param search строка поиска
     * @param sort направление сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return список DTO-товаров по размеру страницы
     */
    Flux<ItemDto> getItems(Long cartId, String search, String sort, Integer pageNumber, Integer pageSize);

    /**
     * Возвращает пейджинг товаров витрины.
     *
     * @param search строка поиска
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return пейджинг товаров витрины
     */
    Mono<ItemsPagingDto> getItemsPaging(String search, Integer pageNumber, Integer pageSize);

    /**
     * Возвращает DTO-товар.
     *
     * @param cartId идентификатор корзины
     * @param itemId идентификатор товара.
     * @return DTO-описание товара
     */
    Mono<ItemDto> getItem(Long cartId, Long itemId);

    /**
     * Создает новый товар и возвращает его DTO-представление.
     *
     * @param title наименование товара
     * @param description описание товара
     * @param price цена товара
     * @return DTO-представление созданного товара
     */
    Mono<ItemDto> createItem(String title, String description, BigDecimal price);
}
