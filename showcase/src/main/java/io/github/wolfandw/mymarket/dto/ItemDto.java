package io.github.wolfandw.mymarket.dto;

/**
 * DTO-описание товара.
 */
public class ItemDto {
    private final Long id;
    private final String title;
    private final String description;
    private final long price;
    private final int count;

    /**
     * DTO-описание товара.
     *
     * @param id          идентификатор товара
     * @param title       название товара
     * @param description описание товара
     * @param price       цена товара
     * @param count       число товаров в корзине
     */
    public ItemDto(Long id,
                   String title,
                   String description,
                   long price,
                   int count) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.count = count;
    }

    /**
     * DTO-описание товара.
     *
     * @param id          идентификатор товара
     * @param title       название товара
     * @param description описание товара
     * @param price       цена товара
     */
    public ItemDto(Long id,
                   String title,
                   String description,
                   long price) {
        this(id, title, description, price, 0);
    }

    /**
     * Возвращает идентификатор товара.
     *
     * @return идентификатор товара
     */
    public Long id() {
        return id;
    }

    /**
     * Возвращает название товара.
     * Для кода и шаблонов.
     *
     * @return название товара
     */
    public String title() {
        return title;
    }

    /**
     * Возвращает название товара.
     * Для тестов.
     *
     * @return название товара
     */
    public String getTitle() {
        return title;
    }

    /**
     * Возвращает описание товара.
     *
     * @return описание товара
     */
    public String description() {
        return description;
    }

    /**
     * Возвращает цену товара.
     *
     * @return цена товара
     */
    public long price() {
        return price;
    }

    /**
     * Возвращает количество товара в корзине.
     * Для шаблонов и кода.
     *
     * @return количество товара
     */
    public int count() {
        return count;
    }

    /**
     * Возвращает количество товара в корзине.
     * Для тестов.
     *
     * @return количество товара
     */
    public int getCount() {
        return count;
    }

    /**
     * Устанавливает количество товара в корзине.
     *
     * @param count количество товара
     */
    public void setCount(Integer count) {
    }
}
