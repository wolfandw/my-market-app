package io.github.wolfandw.mymarket.service;

import io.github.wolfandw.mymarket.dto.ItemDto;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Сервис для работы с товарами.
 */
public interface ItemService {
    /**
     * Возвращает DTO-страницу товаров.
     *
     * @param cartId идентификатор корзины
     * @param search строка поиска
     * @param sort направление сортировки
     * @param pageNumber номер страницы
     * @param pageSize размер страницы
     * @return DTO-описание страницы товаров
     */
    Flux<ItemDto> getItems(Long cartId, String search, String sort, Integer pageNumber, Integer pageSize);

    /**
     * Возвращает размер ассортимента товаров витрины.
     *
     * @param search строка поиска
     * @return размер ассортимента товаров витрины
     */
    Mono<Long> getItemsCount(String search);

//    /**
//     * Возвращает DTO-товар.
//     *
//     * @param cartId идентификатор корзины
//     * @param id     идентификатор товара.
//     * @return DTO-описание товара
//     */
//    Optional<ItemDto> getItem(Long cartId, Long id);
//
//    /**
//     * Создает новый товар и возвращает его DTO-представление.
//     *
//     * @param title наименование товара
//     * @param description описание товара
//     * @param price цена товара
//     * @return DTO-представление созданного товара
//     */
//    ItemDto createItem(String title, String description, BigDecimal price);
}
