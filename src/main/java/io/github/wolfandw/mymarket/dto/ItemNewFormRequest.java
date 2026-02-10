package io.github.wolfandw.mymarket.dto;

/**
 * Параметры запроса формы нового товара.
 */
public class ItemNewFormRequest {
    private String title;
    private String description;
    private Long price;

    /**
     * Возвращает название нового товара.
     *
     * @return название нового товара
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название нового товара.
     *
     * @param title название нового товара
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Возвращает описание нового товара.
     *
     * @return описание нового товара
     */
    public String getDescription() {
        return description;
    }

    /**
     * Устанавливает описание нового товара.
     *
     * @param description описание нового товара
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Возвращает цену нового товара.
     *
     * @return цена нового товара
     */
    public Long getPrice() {
        return price;
    }

    /**
     * Устанавливает цену нового товара.
     *
     * @param price цена нового товара
     */
    public void setPrice(Long price) {
        this.price = price;
    }
}
