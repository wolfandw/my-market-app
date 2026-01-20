package io.github.wolfandw.mymarket.dto;

/**
 * DTO-описание товара.
 *
 * @param id идентификатор товара
 * @param title название товара
 * @param description описание товара
 * @param imgPath путь к картинке товара
 * @param price цена товара
 * @param count число товаров в корзине
 */
public record ItemDto(Long id,
                      String title,
                      String description,
                      String imgPath,
                      long price,
                      int count) {
}
